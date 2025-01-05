package View;

import javax.swing.*;
import javax.swing.table.*;

import DAO.CustomerDAO;
import DBManager.DBConnection;
import Helper.ButtonRenderer;
import Model.Customer;
import View.AccountManagementPanel.ButtonEditor;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class OrderManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private int currentPage = 1;
    private int totalRows;
    private final int rowsPerPage = 10;

    public OrderManagementPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add New Order");

        searchButton.addActionListener(e -> searchOrders());
        addButton.addActionListener(e -> showAddOrderDialog());

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(addButton, BorderLayout.EAST);

        tableModel = new DefaultTableModel(new String[]{"Functions", "ID", "Order Date", "Total Amount", "Customer ID", "Phone", "Name"}, 0);
        table = new JTable(tableModel);
        table.getColumn("Functions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Functions").setCellEditor(new ButtonEditor(new JCheckBox()));
        table.setRowHeight(40);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);  // Adjust the width as needed

        // Set button renderers for the "Functions" column
        table.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(0).setCellEditor(new ButtonEditor(new JCheckBox()));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadOrders();
        addPaginationControls();
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT o.id, o.order_date, o.total_amount, o.customer_id, c.name, c.phone " +
                     "FROM [Order] o " +
                     "LEFT JOIN Customer c ON o.customer_id = c.id " +
                     "ORDER BY o.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            ps.setInt(1, (currentPage - 1) * rowsPerPage);
            ps.setInt(2, rowsPerPage);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add("Edit / Delete");
                row.add(rs.getInt("id"));
                row.add(rs.getDate("order_date"));
                row.add(rs.getFloat("total_amount"));
                row.add(rs.getInt("customer_id"));
                
                // Handle case where customer info might be null
                String customerName = rs.getString("name");
                String customerPhone = rs.getString("phone");
                
                if (customerName == null || customerPhone == null) {
                    customerName = "No customer found";
                    customerPhone = "N/A";
                }
                
                row.add(customerName);  // Customer Name
                row.add(customerPhone);  // Customer Phone Number
                
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
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM [Order]")) {
            if (rs.next()) {
                totalRows = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchOrders() {
        String searchTerm = searchField.getText();
        tableModel.setRowCount(0);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT o.id, o.order_date, o.total_amount, o.customer_id, c.name, c.phone_number " +
                     "FROM [Order] o " +
                     "JOIN Customer c ON o.customer_id = c.id " +
                     "WHERE c.name LIKE ? OR c.phone_number LIKE ? OR o.total_amount LIKE ?")) {
            ps.setString(1, "%" + searchTerm + "%");
            ps.setString(2, "%" + searchTerm + "%");
            ps.setString(3, "%" + searchTerm + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add("Edit / Delete");
                row.add(rs.getInt("id"));
                row.add(rs.getDate("order_date"));
                row.add(rs.getFloat("total_amount"));
                row.add(rs.getInt("customer_id"));
                row.add(rs.getString("name"));  // Customer Name
                row.add(rs.getString("phone_number"));  // Customer Phone Number
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddOrderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Order", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField dateField = new JTextField();
        JTextField totalAmountField = new JTextField();
        JTextField customerIdField = new JTextField();

        dialog.add(new JLabel("Order Date (YYYY-MM-DD):"));
        dialog.add(dateField);
        dialog.add(new JLabel("Total Amount:"));
        dialog.add(totalAmountField);
        dialog.add(new JLabel("Customer ID:"));
        dialog.add(customerIdField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String orderDate = dateField.getText();
            float totalAmount = Float.parseFloat(totalAmountField.getText());
            int customerId = Integer.parseInt(customerIdField.getText());
            addNewOrder(orderDate, totalAmount, customerId);
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addNewOrder(String orderDate, float totalAmount, int customerId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO [Order] (order_date, total_amount, customer_id) VALUES (?, ?, ?)")) {
            ps.setString(1, orderDate);
            ps.setFloat(2, totalAmount);
            ps.setInt(3, customerId);
            ps.executeUpdate();
            loadOrders();
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
                loadOrders();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < Math.ceil((double) totalRows / rowsPerPage)) {
                currentPage++;
                loadOrders();
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
                int orderId = (int) table.getValueAt(row, 1);
                showEditOrderDialog(orderId);
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                int orderId = (int) table.getValueAt(row, 1);
                showDeleteOrderDialog(orderId);
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

    private void showEditOrderDialog(int orderId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM [Order] WHERE id = ?")) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            CustomerDAO dao = new CustomerDAO();
            if (rs.next()) {
                String orderDate = rs.getString("order_date");
                float totalAmount = rs.getFloat("total_amount");
                int customerId = rs.getInt("customer_id");
                
                Customer customer = dao.getCustomerById(customerId);
                String customerPhone = "";
                if (customer != null)
                {
                	customerPhone = customer.phone;
                }
                // Fetch customer phone number based on customerId
                

                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Order", true);
                dialog.setLayout(new GridLayout(5, 2, 10, 10));

                JTextField dateField = new JTextField(orderDate);
                JTextField totalAmountField = new JTextField(String.valueOf(totalAmount));
                JTextField phoneField = new JTextField(customerPhone);  // Phone number field
                JTextField customerIdField = new JTextField(String.valueOf(customerId));  // Hidden Customer ID field
                customerIdField.setEditable(false);  // Make the Customer ID field non-editable

                JButton checkButton = new JButton("Check");
                checkButton.addActionListener(e -> {
                    String phoneNumber = phoneField.getText();
                    Customer c = dao.getCustomerByPhone(phoneNumber);
                    if (c == null) return;
                    int customerIdFromPhone = c.id;
                    if (customerIdFromPhone != -1) {
                        customerIdField.setText(String.valueOf(customerIdFromPhone));
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Customer not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                dialog.add(new JLabel("Order Date:"));
                dialog.add(dateField);
                dialog.add(new JLabel("Total Amount:"));
                dialog.add(totalAmountField);
                dialog.add(new JLabel("Phone Number:"));
                dialog.add(phoneField);
                dialog.add(new JLabel("Customer ID:"));
                dialog.add(customerIdField);
                dialog.add(new JLabel());
                dialog.add(checkButton);

                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(e -> {
                    String newOrderDate = dateField.getText();
                    float newTotalAmount = Float.parseFloat(totalAmountField.getText());
                    int newCustomerId = Integer.parseInt(customerIdField.getText());
                    updateOrder(orderId, newOrderDate, newTotalAmount, newCustomerId);
                    dialog.dispose();
                });

                dialog.add(new JLabel());
                dialog.add(saveButton);

                dialog.setSize(300, 250);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin đơn hàng
    private void updateOrder(int orderId, String orderDate, float totalAmount, int customerId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE [Order] SET order_date = ?, total_amount = ?, customer_id = ? WHERE id = ?")) {
            ps.setString(1, orderDate);
            ps.setFloat(2, totalAmount);
            ps.setInt(3, customerId);
            ps.setInt(4, orderId);
            ps.executeUpdate();
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hiển thị hộp thoại xác nhận xóa đơn hàng
    private void showDeleteOrderDialog(int orderId) {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this order?", "Delete Order", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            deleteOrder(orderId);
        }
    }

    // Xóa đơn hàng khỏi cơ sở dữ liệu
    private void deleteOrder(int orderId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM [Order] WHERE id = ?")) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
