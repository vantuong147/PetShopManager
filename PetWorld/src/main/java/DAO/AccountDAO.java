package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DBManager.DBConnection;
import Model.Account;

public class AccountDAO {

	public void addAccount(Account account) {
	    String query = "INSERT INTO Account (username, passw, account_type) VALUES (?, ?, ?)";
	    
	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
	        
	        // Gán các giá trị cho câu lệnh SQL
	        ps.setString(1, account.username);
	        ps.setString(2, account.password);
	        ps.setString(3, account.accountType);
	        
	        // Thực thi câu lệnh
	        int affectedRows = ps.executeUpdate();
	        
	        // Nếu có dòng bị ảnh hưởng (tức là đã chèn thành công)
	        if (affectedRows > 0) {
	            // Lấy ID được tạo ra sau khi thêm một bản ghi
	            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    // Lấy giá trị id của tài khoản mới thêm vào
	                    int generatedId = generatedKeys.getInt(1);
	                    account.id = generatedId; // Gán ID mới cho đối tượng Account
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


    public void updateAccount(Account account) {
        String query = "UPDATE Account SET username = ?, passw = ?, account_type = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, account.username);
            ps.setString(2, account.password);
            ps.setString(3, account.accountType);
            ps.setInt(4, account.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(int id) {
        String query = "DELETE FROM Account WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account getAccountById(int id) {
        String query = "SELECT * FROM Account WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("passw"),
                        rs.getString("account_type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Account> getAllAccounts() {
    	ArrayList<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM Account";
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Account account = new Account(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("passw"),
                        rs.getString("account_type")
                );
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }
}
