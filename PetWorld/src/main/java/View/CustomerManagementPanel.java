package View;

import javax.swing.*;
import javax.swing.table.*;
import DBManager.DBConnection;
import Helper.ButtonRenderer;
import View.AccountManagementPanel.ButtonEditor;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CustomerManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private int currentPage = 1;
    private int totalRows;
    private final int rowsPerPage = 10;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add New Customer");

        searchButton.addActionListener(e -> searchCustomers());
        addButton.addActionListener(e -> showAddCustomerDialog());

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(addButton, BorderLayout.EAST);

        tableModel = new DefaultTableModel(new String[]{"Functions", "ID", "Name", "Phone"}, 0);
        table = new JTable(tableModel);
        table.getColumn("Functions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Functions").setCellEditor(new ButtonEditor(new JCheckBox()));
        table.setRowHeight(40);

        // Set button renderers for the "Functions" column
        table.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(0).setCellEditor(new ButtonEditor(new JCheckBox()));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadCustomers();
        addPaginationControls();
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM Customer ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            ps.setInt(1, (currentPage - 1) * rowsPerPage);
            ps.setInt(2, rowsPerPage);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Add buttons to the "Functions" column
                Vector<Object> row = new Vector<>();
                row.add("Edit / Delete");
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("phone"));
                tableModel.addRow(row);
            }
            updateTotalRows();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalRows() {
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM Customer")) {
            if (rs.next()) {
                totalRows = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchCustomers() {
        String searchTerm = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Customer WHERE name LIKE ? OR phone LIKE ?")) {
            ps.setString(1, "%" + searchTerm + "%");
            ps.setString(2, "%" + searchTerm + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add("Edit / Delete");
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("phone"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Customer", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            addNewCustomer(name, phone);
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addNewCustomer(String name, String phone) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Customer (name, phone) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.executeUpdate();
            loadCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPaginationControls() {
        JPanel paginationPanel = new JPanel();
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadCustomers();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < Math.ceil((double) totalRows / rowsPerPage)) {
                currentPage++;
                loadCustomers();
            }
        });

        paginationPanel.add(prevButton);
        paginationPanel.add(new JLabel("Page: " + currentPage));
        paginationPanel.add(nextButton);

        add(paginationPanel, BorderLayout.SOUTH);
    }

    // Render buttons inside table
    private class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton = new JButton("Edit");
        private JButton deleteButton = new JButton("Delete");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Edit and delete button editor
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton editButton;
        protected JButton deleteButton;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            editButton = new JButton();
            deleteButton = new JButton();
            editButton.setOpaque(true);
            deleteButton.setOpaque(true);

            editButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                int customerId = (int) table.getValueAt(row, 1);
                showEditCustomerDialog(customerId);
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                int customerId = (int) table.getValueAt(row, 1);
                showDeleteCustomerDialog(customerId);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editButton.setText("Edit");
            deleteButton.setText("Delete");
            isPushed = true;
            JPanel panel = new JPanel();
            panel.add(editButton);
            panel.add(deleteButton);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle the action on button press (Edit/Delete)
            }
            isPushed = false;
            return null;
        }
    }

    // Hiển thị hộp thoại chỉnh sửa thông tin khách hàng
    private void showEditCustomerDialog(int customerId) {
        // Lấy thông tin khách hàng từ cơ sở dữ liệu
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM Customer WHERE id = ?")) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");

                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Customer", true);
                dialog.setLayout(new GridLayout(3, 2, 10, 10));

                JTextField nameField = new JTextField(name);
                JTextField phoneField = new JTextField(phone);

                dialog.add(new JLabel("Name:"));
                dialog.add(nameField);
                dialog.add(new JLabel("Phone:"));
                dialog.add(phoneField);

                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(e -> {
                    String newName = nameField.getText();
                    String newPhone = phoneField.getText();
                    updateCustomer(customerId, newName, newPhone);
                    dialog.dispose();
                });

                dialog.add(new JLabel());
                dialog.add(saveButton);

                dialog.setSize(300, 150);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin khách hàng
    private void updateCustomer(int customerId, String name, String phone) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE Customer SET name = ?, phone = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, customerId);
            ps.executeUpdate();
            loadCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hiển thị hộp thoại xác nhận xóa khách hàng
    private void showDeleteCustomerDialog(int customerId) {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Delete Customer", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            deleteCustomer(customerId);
        }
    }

    // Xóa khách hàng khỏi cơ sở dữ liệu
    private void deleteCustomer(int customerId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM Customer WHERE id = ?")) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
            loadCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
