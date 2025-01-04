package DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Helper.Debug;
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


}
