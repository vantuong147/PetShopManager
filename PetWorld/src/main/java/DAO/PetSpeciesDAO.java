package DAO;

import Model.PetSpecies;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DBManager.DBConnection;

public class PetSpeciesDAO {
	// Method to get all pet species
	public List<PetSpecies> getAllPetSpecies() {
		List<PetSpecies> petSpeciesList = new ArrayList<>();
		String sql = "SELECT * FROM PetSpecies";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				int id = rs.getInt("id");
				String speciesName = rs.getString("species_name");
				float avgMinPrice = rs.getFloat("avg_min_price");
				float avgMaxPrice = rs.getFloat("avg_max_price");
				float avgWeight = rs.getFloat("avg_weight");
				int avgMaxAge = rs.getInt("avg_max_age");
				String image_url = rs.getString("image_url");
                String des = rs.getString("des");

				PetSpecies petSpecies = new PetSpecies(id, speciesName, avgMinPrice, avgMaxPrice, avgWeight, avgMaxAge, image_url, des);
				petSpeciesList.add(petSpecies);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return petSpeciesList;
	}

	// Method to get a pet species by ID
	public PetSpecies getPetSpeciesById(int id) {
		PetSpecies petSpecies = null;
		String sql = "SELECT * FROM PetSpecies WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, id);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String speciesName = rs.getString("species_name");
					float avgMinPrice = rs.getFloat("avg_min_price");
					float avgMaxPrice = rs.getFloat("avg_max_price");
					float avgWeight = rs.getFloat("avg_weight");
					int avgMaxAge = rs.getInt("avg_max_age");
					String image_url = rs.getString("image_url");
	                String des = rs.getString("des");
					petSpecies = new PetSpecies(id, speciesName, avgMinPrice, avgMaxPrice, avgWeight, avgMaxAge, image_url, des);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return petSpecies;
	}

	// Method to insert a new pet species
	public boolean insertPetSpecies(PetSpecies petSpecies) {
		String sql = "INSERT INTO PetSpecies (species_name, avg_min_price, avg_max_price, avg_weight, avg_max_age) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, petSpecies.getSpeciesName());
			pstmt.setFloat(2, petSpecies.getAvgMinPrice());
			pstmt.setFloat(3, petSpecies.getAvgMaxPrice());
			pstmt.setFloat(4, petSpecies.getAvgWeight());
			pstmt.setInt(5, petSpecies.getAvgMaxAge());

			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Method to update a pet species
	public boolean updatePetSpecies(PetSpecies petSpecies) {
		String sql = "UPDATE PetSpecies SET species_name = ?, avg_min_price = ?, avg_max_price = ?, avg_weight = ?, avg_max_age = ? WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, petSpecies.getSpeciesName());
			pstmt.setFloat(2, petSpecies.getAvgMinPrice());
			pstmt.setFloat(3, petSpecies.getAvgMaxPrice());
			pstmt.setFloat(4, petSpecies.getAvgWeight());
			pstmt.setInt(5, petSpecies.getAvgMaxAge());
			pstmt.setInt(6, petSpecies.getId());

			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Method to delete a pet species
	public boolean deletePetSpecies(int id) {
		String sql = "DELETE FROM PetSpecies WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, id);

			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}
