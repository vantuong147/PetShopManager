package View;

import javax.swing.*;

import DAO.PetDAO;
import DAO.PetSpeciesDAO;
import Model.Pet;
import Model.PetSpecies;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AddPetPanel extends JPanel {
    private JTextField txtPetName, txtPrice, txtAge, txtColor, txtWeight, txtImages;
    private JTextArea txtDescription;
    private JComboBox<String> cbPetSpecies;
    private JButton btnAddPet;

    public AddPetPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblPetName = new JLabel("Pet Name:");
        txtPetName = new JTextField(20);

        JLabel lblPrice = new JLabel("Price:");
        txtPrice = new JTextField(20);

        JLabel lblAge = new JLabel("Age:");
        txtAge = new JTextField(20);

        JLabel lblColor = new JLabel("Color (Hex):");
        txtColor = new JTextField(20);

        JLabel lblWeight = new JLabel("Weight:");
        txtWeight = new JTextField(20);

        JLabel lblImages = new JLabel("Image URLs:");
        txtImages = new JTextField(20);

        JLabel lblDescription = new JLabel("Description:");
        txtDescription = new JTextArea(3, 20);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setLineWrap(true);

        JLabel lblPetSpecies = new JLabel("Pet Species:");
        cbPetSpecies = new JComboBox<>();
        loadPetSpecies();

        btnAddPet = new JButton("Add Pet");
        btnAddPet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPetToDatabase();
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; add(lblPetName, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(txtPetName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; add(lblPrice, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(txtPrice, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; add(lblAge, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(txtAge, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(lblColor, gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(txtColor, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(lblWeight, gbc);
        gbc.gridx = 1; gbc.gridy = 4; add(txtWeight, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(lblImages, gbc);
        gbc.gridx = 1; gbc.gridy = 5; add(txtImages, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; add(lblDescription, gbc);
        gbc.gridx = 1; gbc.gridy = 6; add(new JScrollPane(txtDescription), gbc);

        gbc.gridx = 0; gbc.gridy = 8; add(lblPetSpecies, gbc);
        gbc.gridx = 1; gbc.gridy = 8; add(cbPetSpecies, gbc);
        
        gbc.gridx = 1; gbc.gridy = 9; add(btnAddPet, gbc);
    }

    private void loadPetSpecies() {
        ArrayList<PetSpecies> speciesList = new PetSpeciesDAO().getAllPetSpecies();
        for (PetSpecies species : speciesList) {
            cbPetSpecies.addItem(species.id + "_" + species.getSpeciesName());
        }
    }

    private void addPetToDatabase() {    	
        String petName = txtPetName.getText();
        float price = Float.parseFloat(txtPrice.getText());
        int age = Integer.parseInt(txtAge.getText());
        String color = txtColor.getText();
        String description = txtDescription.getText();
        String speciesName = (String) cbPetSpecies.getSelectedItem();
        
        int specID = Integer.parseInt(speciesName.split("_")[0]);
        float weight = Float.parseFloat(txtWeight.getText());
        String images = txtImages.getText();

        Pet newPet = new Pet(0, petName, price,age,color.toString(), description, "NOT_SALED", specID, images, weight);
        new PetDAO().addPet(newPet);
        JOptionPane.showMessageDialog(this, "Pet added successfully!");
    }
}
