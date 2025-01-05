package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import DBManager.DBConnection;
import Helper.ButtonRenderer;

import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.EventObject;
import java.util.Vector;

public class AccountManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public AccountManagementPanel() { 
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Action", "ID", "Username", "Password", "Account Type"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JButton addButton = new JButton("Add New Account");
        addButton.addActionListener(e -> showAddAccountDialog());

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(addButton, BorderLayout.NORTH);

        loadAccounts();
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Account")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(new JButton("Edit/Delete"));
                row.add(rs.getInt("id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("passw"));
                row.add(rs.getString("account_type"));

                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddAccountDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Account", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JComboBox<String> accountTypeBox = new JComboBox<>(new String[]{"ADMIN", "STAFF"});

        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Account Type:"));
        dialog.add(accountTypeBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String accountType = (String) accountTypeBox.getSelectedItem();
            addNewAccount(username, password, accountType);
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addNewAccount(String username, String password, String accountType) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Account (username, passw, account_type) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, accountType);
            ps.executeUpdate();
            loadAccounts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showEditAccountDialog(int accountId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM Account WHERE id = ?")) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("passw");
                String accountType = rs.getString("account_type");
                showEditAccountDialog(accountId, username, password, accountType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showEditAccountDialog(int accountId, String username, String password, String accountType) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Account", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField usernameField = new JTextField(username);
        JTextField passwordField = new JTextField(password);
        JComboBox<String> accountTypeBox = new JComboBox<>(new String[]{"ADMIN", "STAFF"});
        accountTypeBox.setSelectedItem(accountType);

        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Account Type:"));
        dialog.add(accountTypeBox);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "UPDATE Account SET username = ?, passw = ?, account_type = ? WHERE id = ?")) {
                ps.setString(1, usernameField.getText());
                ps.setString(2, passwordField.getText());
                ps.setString(3, (String) accountTypeBox.getSelectedItem());
                ps.setInt(4, accountId);
                ps.executeUpdate();
                loadAccounts();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteAccount(int accountId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this account?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM Account WHERE id = ?")) {
                ps.setInt(1, accountId);
                ps.executeUpdate();
                loadAccounts();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

	
	// Editor cho nút trong JTable
	class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		private JPanel panel;
		private JButton editButton, deleteButton;
		private int currentRow;
		
		public ButtonEditor(JCheckBox checkBox) {
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			editButton = new JButton("Edit");
			deleteButton = new JButton("Delete");
			editButton.addActionListener(this);
			deleteButton.addActionListener(this);
			panel.add(editButton);
			panel.add(deleteButton);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			currentRow = row;
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return "Edit/Delete";
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int accountId = (int) table.getValueAt(currentRow, 1);
			if (e.getSource() == editButton) {
				showEditAccountDialog(accountId);
				fireEditingStopped();
			} else if (e.getSource() == deleteButton) {
				deleteAccount(accountId);
			}

		}
	}
}
