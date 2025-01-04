package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DBManager.DBConnection;
import Model.OrderDetail;

public class OrderDetailDAO {

    public void addOrderDetail(OrderDetail orderDetail) {
        String query = "INSERT INTO OrderDetail (sale_price, order_id, pet_id) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setFloat(1, orderDetail.salePrice);
            ps.setInt(2, orderDetail.orderId);
            ps.setInt(3, orderDetail.petId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderDetail.id = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderDetail(OrderDetail orderDetail) {
        String query = "UPDATE OrderDetail SET sale_price = ?, order_id = ?, pet_id = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setFloat(1, orderDetail.salePrice);
            ps.setInt(2, orderDetail.orderId);
            ps.setInt(3, orderDetail.petId);
            ps.setInt(4, orderDetail.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrderDetail(int id) {
        String query = "DELETE FROM OrderDetail WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public OrderDetail getOrderDetailById(int id) {
        String query = "SELECT * FROM OrderDetail WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new OrderDetail(rs.getInt("id"), rs.getFloat("sale_price"), rs.getInt("order_id"), rs.getInt("pet_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OrderDetail> getAllOrderDetails() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT * FROM OrderDetail";
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail(rs.getInt("id"), rs.getFloat("sale_price"),
                        rs.getInt("order_id"), rs.getInt("pet_id"));
                orderDetails.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }
}
