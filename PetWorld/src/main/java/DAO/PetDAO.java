package DAO;

import java.sql.*;
import java.util.ArrayList;
import DBManager.DBConnection;
import Model.Pet;

public class PetDAO {

	public void addPet(Pet pet) {
	    String query = "INSERT INTO Pet (pet_name, price, age, color, description, state, pet_species_id, images, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

	        ps.setString(1, pet.petName);
	        ps.setFloat(2, pet.price);
	        ps.setInt(3, pet.age);
	        ps.setString(4, pet.color);
	        ps.setString(5, pet.description);
	        ps.setString(6, pet.state);
	        ps.setInt(7, pet.petSpeciesId);
	        ps.setString(8, pet.images);
	        ps.setFloat(9, pet.weight);

	        int affectedRows = ps.executeUpdate();

	        if (affectedRows > 0) {
	            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    pet.id = generatedKeys.getInt(1);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public void updatePet(Pet pet) {
	    String query = "UPDATE Pet SET pet_name = ?, price = ?, age = ?, color = ?, description = ?, state = ?, pet_species_id = ?, images = ?, weight = ? WHERE id = ?";
	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setString(1, pet.petName);
	        ps.setFloat(2, pet.price);
	        ps.setInt(3, pet.age);
	        ps.setString(4, pet.color);
	        ps.setString(5, pet.description);
	        ps.setString(6, pet.state);
	        ps.setInt(7, pet.petSpeciesId);
	        ps.setString(8, pet.images);
	        ps.setFloat(9, pet.weight);
	        ps.setInt(10, pet.id);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


    public void deletePet(int id) {
        String query = "DELETE FROM Pet WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Pet getPetById(int id) {
        String query = "SELECT * FROM Pet WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Pet(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getFloat("price"),
                        rs.getInt("age"),
                        rs.getString("color"),
                        rs.getString("description"),
                        rs.getString("state"),
                        rs.getInt("pet_species_id"),
                        rs.getString("images"),
                        rs.getFloat("weight")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Pet> getAllPets() {
        ArrayList<Pet> petList = new ArrayList<>();
        String query = "SELECT * FROM Pet";
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Pet pet = new Pet(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getFloat("price"),
                        rs.getInt("age"),
                        rs.getString("color"),
                        rs.getString("description"),
                        rs.getString("state"),
                        rs.getInt("pet_species_id"),
                        rs.getString("images"),
                        rs.getFloat("weight")
                );
                petList.add(pet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return petList;
    }
    
    public ArrayList<Pet> getAllPetsBySpeicies(int spID) {
        ArrayList<Pet> petList = new ArrayList<>();
        String query = "SELECT * FROM Pet WHERE pet_species_id = " + spID;
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Pet pet = new Pet(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getFloat("price"),
                        rs.getInt("age"),
                        rs.getString("color"),
                        rs.getString("description"),
                        rs.getString("state"),
                        rs.getInt("pet_species_id"),
                        rs.getString("images"),
                        rs.getFloat("weight")
                );
                petList.add(pet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return petList;
    }
}
