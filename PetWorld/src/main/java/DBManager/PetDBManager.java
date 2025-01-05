package DBManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Helper.Debug;
import Model.Pet;
import Model.PetSpecies;

public class PetDBManager {
	public static ArrayList<PetSpecies> getPetSpeciesWithUnsoldAndTotalPetsCount() {
	    ArrayList<PetSpecies> petSpeciesList = new ArrayList<>();

	    // Bước 1: Lấy tất cả PetSpecies từ bảng PetSpecies
	    String sqlSelectAllPetSpecies = "SELECT id, species_name, avg_min_price, avg_max_price, avg_weight, avg_max_age, image_url, des FROM PetSpecies";

	    try (Connection conn = DBConnection.getConnection(); 
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sqlSelectAllPetSpecies)) {

	        // Duyệt qua tất cả các PetSpecies
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String speciesName = rs.getString("species_name");
	            float avgMinPrice = rs.getFloat("avg_min_price");
	            float avgMaxPrice = rs.getFloat("avg_max_price");
	            float avgWeight = rs.getFloat("avg_weight");
	            int avgMaxAge = rs.getInt("avg_max_age");
	            String image_url = rs.getString("image_url");
	            String des = rs.getString("des");

	            // Tạo đối tượng PetSpecies
	            PetSpecies petSpecies = new PetSpecies(id, speciesName, avgMinPrice, avgMaxPrice, avgWeight, avgMaxAge, image_url, des);

	            // Bước 2: Truy vấn tổng số pet và số pet chưa bán cho từng PetSpecies
	            String sqlSelectPetCounts = "SELECT COUNT(p.id) AS total_pet_count, " +
	                                        "COUNT(CASE WHEN p.state = 'NOT_SALED' THEN 1 END) AS unsold_pet_count " +
	                                        "FROM Pet p WHERE p.pet_species_id = ?";
	            
	            try (PreparedStatement ps = conn.prepareStatement(sqlSelectPetCounts)) {
	                ps.setInt(1, id); // Set id của PetSpecies vào câu truy vấn

	                try (ResultSet petCountsRs = ps.executeQuery()) {
	                    if (petCountsRs.next()) {
	                        int totalPetCount = petCountsRs.getInt("total_pet_count");
	                        int unsoldPetCount = petCountsRs.getInt("unsold_pet_count");

	                        // Cập nhật số lượng pet vào đối tượng PetSpecies
	                        petSpecies.SetQuantity(totalPetCount, unsoldPetCount);
	                    }
	                }
	            }

	            // Thêm PetSpecies vào danh sách
	            petSpeciesList.add(petSpecies);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // Bước 3: Trả về danh sách PetSpecies đã được cập nhật
	    return petSpeciesList;
	}
	
	public static Object[] confirmSellPet(ArrayList<Pet> selectedPets, int customerId, int staffId) {
	    String orderQuery = "INSERT INTO [Order] (order_date, total_amount, customer_id, staff_id) VALUES (?, ?, ?, ?)";
	    String orderDetailQuery = "INSERT INTO OrderDetail (sale_price, order_id, pet_id) VALUES (?, ?, ?)";
	    String updatePetQuery = "UPDATE Pet SET state = 'SALED' WHERE id = ?";
	    String getStaffQuery = "SELECT username FROM Account WHERE id = ?";
	    Connection connection = DBConnection.getConnection();
	    boolean result = true;
	    String invoiceFilePath = null;
	    
	    try {
	        // Bắt đầu transaction
	        connection.setAutoCommit(false);

	        // Tính toán tổng giá trị đơn hàng
	        float totalAmount = 0;
	        for (Pet pet : selectedPets) {
	            totalAmount += pet.price;
	        }

	        // Tạo đơn hàng
	        try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
	            orderStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
	            orderStmt.setFloat(2, totalAmount);
	            orderStmt.setInt(3, customerId);
	            orderStmt.setInt(4, staffId);
	            orderStmt.executeUpdate();

	            // Lấy ID của đơn hàng vừa tạo
	            try (ResultSet rs = orderStmt.getGeneratedKeys()) {
	                if (rs.next()) {
	                    int orderId = rs.getInt(1);

	                    // Thêm chi tiết đơn hàng cho mỗi con vật trong selectedPets
	                    try (PreparedStatement orderDetailStmt = connection.prepareStatement(orderDetailQuery)) {
	                        for (Pet pet : selectedPets) {
	                            // Cập nhật trạng thái pet thành "SALED"
	                            try (PreparedStatement updatePetStmt = connection.prepareStatement(updatePetQuery)) {
	                                updatePetStmt.setInt(1, pet.id);
	                                updatePetStmt.executeUpdate();
	                            }

	                            // Thêm chi tiết đơn hàng
	                            orderDetailStmt.setFloat(1, pet.price);
	                            orderDetailStmt.setInt(2, orderId);
	                            orderDetailStmt.setInt(3, pet.id);
	                            orderDetailStmt.addBatch();
	                        }

	                        // Thực thi tất cả các bản ghi OrderDetail
	                        orderDetailStmt.executeBatch();
	                    }

	                    // Tạo hóa đơn đơn giản và ghi vào file
	                    try (PreparedStatement getStaffStmt = connection.prepareStatement(getStaffQuery)) {
	                        getStaffStmt.setInt(1, staffId);
	                        ResultSet staffRs = getStaffStmt.executeQuery();
	                        if (staffRs.next()) {
	                            String staffName = staffRs.getString("username");
	                            invoiceFilePath = createInvoice(orderId, totalAmount, staffName, selectedPets);
	                        }
	                    }
	                }
	            }
	        }

	        // Commit transaction nếu mọi thứ thành công
	        connection.commit();
	        result = true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        try {
	            // Rollback nếu có lỗi xảy ra
	            connection.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	        result = false;
	    } finally {
	        try {
	            // Đảm bảo bật lại chế độ auto commit
	            DBConnection.getConnection().setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	            result = false;
	        }
	    }

	    // Trả về kết quả và đường dẫn hóa đơn
	    return new Object[] { result, invoiceFilePath };
	}

	private static String createInvoice(int orderId, float totalAmount, String staffName, ArrayList<Pet> selectedPets) {
	    // Định dạng thời gian theo ngày tháng năm
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    String timestamp = dateFormat.format(new Date());

	    // Đường dẫn thư mục và file hóa đơn
	    String directoryPath = "Invoices/" + timestamp;
	    
	    // Tạo thư mục Invoices/{Timestamp} nếu chưa tồn tại
	    File directory = new File(directoryPath);
	    if (!directory.exists()) {
	        directory.mkdirs();
	    }

	    // Đường dẫn file hóa đơn
	    String invoiceFilePath = directoryPath + "/Invoice_Order_" + orderId + ".txt";
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(invoiceFilePath))) {
	        writer.write("Invoice for Order ID: " + orderId);
	        writer.newLine();
	        writer.write("Staff: " + staffName);
	        writer.newLine();
	        writer.write("Date: " + new Timestamp(System.currentTimeMillis()).toString());
	        writer.newLine();
	        writer.write("---------------------------------------------------");
	        writer.newLine();
	        
	        writer.write("Pet Details:");
	        writer.newLine();
	        for (Pet pet : selectedPets) {
	            writer.write("ID: " + pet.id + " | Name: " + pet.petName + " | Price: $" + pet.price);
	            writer.newLine();
	        }
	        
	        writer.write("---------------------------------------------------");
	        writer.newLine();
	        writer.write("Total Amount: $" + totalAmount);
	        writer.newLine();
	        
	        writer.write("Thank you for your purchase!");
	        writer.newLine();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return invoiceFilePath;  // Trả về đường dẫn file hóa đơn
	}

    public static int findOrCreateCustomer(String name, String phone) {
        // Kiểm tra xem khách hàng có tồn tại trong cơ sở dữ liệu hay không
    	
        Model.Customer customer = new DAO.CustomerDAO().getCustomerByPhone(phone);

        if (customer != null) {
            // Nếu khách hàng đã tồn tại, trả về id khách hàng
            return customer.getId();
        } else {
        	Model.Customer newCustomer = new Model.Customer(0, name, phone);
            new DAO.CustomerDAO().addCustomer(newCustomer);
            return newCustomer.id;
        }
    }



}
