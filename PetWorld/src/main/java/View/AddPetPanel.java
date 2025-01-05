package View;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import DAO.PetDAO;
import DAO.PetSpeciesDAO;
import Model.Pet;
import Model.PetSpecies;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class AddPetPanel extends JPanel {
    private JTextField txtPetName, txtPrice, txtAge, txtColor, txtWeight;
    private JTextArea txtDescription;
    private JComboBox<String> cbPetSpecies;
    private JButton btnAddPet, btnSelectImages, btnAddPetSpecies;
    private JTextField txtImages;

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
        txtImages.setEditable(false);
        btnSelectImages = new JButton("Select Images");
        btnSelectImages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImages();
            }
        });

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

        // Nút thêm loại pet mới
        btnAddPetSpecies = new JButton("Add Pet Species");
        btnAddPetSpecies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddPetSpeciesDialog();
            }
        });

        // Thêm các thành phần vào layout
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
        gbc.gridx = 2; gbc.gridy = 5; add(btnSelectImages, gbc);

        gbc.gridx = 0; gbc.gridy = 6; add(lblDescription, gbc);
        gbc.gridx = 1; gbc.gridy = 6; add(new JScrollPane(txtDescription), gbc);

        gbc.gridx = 0; gbc.gridy = 8; add(lblPetSpecies, gbc);
        gbc.gridx = 1; gbc.gridy = 8; add(cbPetSpecies, gbc);

        gbc.gridx = 1; gbc.gridy = 9; add(btnAddPet, gbc);
        gbc.gridx = 1; gbc.gridy = 10; add(btnAddPetSpecies, gbc);
    }

    private void loadPetSpecies() {
        ArrayList<PetSpecies> speciesList = new PetSpeciesDAO().getAllPetSpecies();
        for (PetSpecies species : speciesList) {
            cbPetSpecies.addItem(species.id + "_" + species.getSpeciesName());
        }
    }

    private void selectImages() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            StringBuilder imagePaths = new StringBuilder();
            for (File file : selectedFiles) {
                if (imagePaths.length() > 0) {
                    imagePaths.append(",");
                }
                imagePaths.append(file.getAbsolutePath());
            }
            txtImages.setText(imagePaths.toString());
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

        Pet newPet = new Pet(0, petName, price, age, color, description, "NOT_SALED", specID, images, weight);
        new PetDAO().addPet(newPet);
        JOptionPane.showMessageDialog(this, "Pet added successfully!");
    }

    // Hàm mở cửa sổ để nhập thông tin loài pet mới
    private void openAddPetSpeciesDialog() {
        JTextField txtSpeciesName = new JTextField(20);
        JTextField txtMinPrice = new JTextField(20);
        JTextField txtMaxPrice = new JTextField(20);
        JTextField txtAvgWeight = new JTextField(20);
        JTextField txtMaxAge = new JTextField(20);
        JTextField txtImageUrl = new JTextField(20);
        JTextArea txtDescription = new JTextArea(3, 20);

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("Species Name:"));
        panel.add(txtSpeciesName);
        panel.add(new JLabel("Min Price:"));
        panel.add(txtMinPrice);
        panel.add(new JLabel("Max Price:"));
        panel.add(txtMaxPrice);
        panel.add(new JLabel("Avg Weight (kg):"));
        panel.add(txtAvgWeight);
        panel.add(new JLabel("Max Age (years):"));
        panel.add(txtMaxAge);
        panel.add(new JLabel("Image URL:"));
        panel.add(txtImageUrl);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(txtDescription));

        int option = JOptionPane.showConfirmDialog(this, panel, "Add Pet Species", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String speciesName = txtSpeciesName.getText();
            float minPrice = Float.parseFloat(txtMinPrice.getText());
            float maxPrice = Float.parseFloat(txtMaxPrice.getText());
            float avgWeight = Float.parseFloat(txtAvgWeight.getText());
            int maxAge = Integer.parseInt(txtMaxAge.getText());
            String imageUrl = txtImageUrl.getText();
            String description = txtDescription.getText();

            PetSpecies newSpecies = new PetSpecies(0, speciesName, minPrice, maxPrice, avgWeight, maxAge, imageUrl, description);
            new PetSpeciesDAO().insertPetSpecies(newSpecies);
            JOptionPane.showMessageDialog(this, "Pet Species added successfully!");
        }
    }
}
