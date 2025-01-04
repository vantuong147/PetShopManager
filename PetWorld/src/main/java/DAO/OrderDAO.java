package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DBManager.DBConnection;
import Model.Order;

public class OrderDAO {

    public void addOrder(Order order) {
        String query = "INSERT INTO [Order] (order_date, total_amount, customer_id, staff_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, order.orderDate);
            ps.setFloat(2, order.totalAmount);
            ps.setInt(3, order.customerId);
            ps.setInt(4, order.staffId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.id = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(Order order) {
        String query = "UPDATE [Order] SET order_date = ?, total_amount = ?, customer_id = ?, staff_id = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setTimestamp(1, order.orderDate);
            ps.setFloat(2, order.totalAmount);
            ps.setInt(3, order.customerId);
            ps.setInt(4, order.staffId);
            ps.setInt(5, order.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int id) {
        String query = "DELETE FROM [Order] WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Order getOrderById(int id) {
        String query = "SELECT * FROM [Order] WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Order(rs.getInt("id"), rs.getTimestamp("order_date"), rs.getFloat("total_amount"),
                        rs.getInt("customer_id"), rs.getInt("staff_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM [Order]";
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Order order = new Order(rs.getInt("id"), rs.getTimestamp("order_date"), rs.getFloat("total_amount"),
                        rs.getInt("customer_id"), rs.getInt("staff_id"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
