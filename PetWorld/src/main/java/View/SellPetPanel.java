package View;

import javax.swing.*;

import DAO.CustomerDAO;
import DAO.PetDAO;
import DAO.PetSpeciesDAO;
import DBManager.PetDBManager;
import Helper.Debug;
import Helper.HelperFunc;
import Model.Customer;
import Model.Pet;
import Model.PetSpecies;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class SellPetPanel extends JPanel {
    private DefaultListModel<Pet> petListModel;
    private JList<Pet> petList;
    private JLabel lblTotalAmount;
    private float totalAmount = 0;
    private ArrayList<Pet> selectedPets = new ArrayList<>();
    
    public SellPetPanel() {
        setLayout(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());

        petListModel = new DefaultListModel<>();
        petList = new JList<>(petListModel);
        leftPanel.add(new JScrollPane(petList), BorderLayout.CENTER);

        // Tạo nút Clear và cố định kích thước
        JButton btnClear = new JButton("Clear");
        btnClear.setPreferredSize(new Dimension(150, 40)); // Đặt kích thước cố định cho nút
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSelectedPets();
            }
        });
        leftPanel.add(btnClear, BorderLayout.NORTH); // Đặt nút Clear ở trên

        // Nút Add Pet
        JButton btnAddPet = new JButton("Add Pet");
        btnAddPet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPetToOrder();
            }
        });
        leftPanel.add(btnAddPet, BorderLayout.SOUTH);

        lblTotalAmount = new JLabel("Total Amount: $0.0");
        rightPanel.add(lblTotalAmount, BorderLayout.NORTH);

        JButton btnConfirmOrder = new JButton("Confirm Order");
        btnConfirmOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmOrder();
            }
        });
        rightPanel.add(btnConfirmOrder, BorderLayout.SOUTH);

        add(leftPanel);
        add(rightPanel);
    }

    public void addPetToOrder() {
        Pet pet = showSelectPetDialog();
        if (pet != null) {
            // Kiểm tra nếu pet đã có trong danh sách selectedPets
            if (selectedPets.contains(pet)) {
                JOptionPane.showMessageDialog(this, "This pet has already been added to the order.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (pet.state == "SALED")
            {
            	JOptionPane.showMessageDialog(this, "This pet has already been sold", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                petListModel.addElement(pet);
                selectedPets.add(pet);
                totalAmount += pet.getPrice();
                lblTotalAmount.setText("Total Amount: $" + HelperFunc.formatCurrency(totalAmount));
            }
        }
    }
    
    public void addPetToOrder(Pet pet) {
    	Debug.Log(pet.state);
        if (pet != null) {
            // Kiểm tra nếu pet đã có trong danh sách selectedPets
            if (selectedPets.contains(pet)) {
                JOptionPane.showMessageDialog(this, "This pet has already been added to the order.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (pet.state.equals("SALED"))
            {
            	JOptionPane.showMessageDialog(this, "This pet has already been sold", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                petListModel.addElement(pet);
                selectedPets.add(pet);
                totalAmount += pet.getPrice();
                lblTotalAmount.setText("Total Amount: $" + HelperFunc.formatCurrency(totalAmount));
                JOptionPane.showMessageDialog(this, "Added Pet to Order");
            }

        }
    }


    private Pet showSelectPetDialog() {
        JDialog dialog = new JDialog((Frame) null, "Select Pet", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.setPreferredSize(new Dimension(600, 400)); // Đặt kích thước ưu tiên lớn hơn
        dialog.setSize(600, 400); // Set width to 600 and height to 400
        dialog.setLocationRelativeTo(null);
        
        JComboBox<String> cbPetSpecies = new JComboBox<>();
        JComboBox<Pet> cbPets = new JComboBox<>();
        
        JLabel lblSpecies = new JLabel("Select Species:");
        JLabel lblPet = new JLabel("Select Pet:");

        // Load pet species into the first combo box
        ArrayList<PetSpecies> speciesList = new PetSpeciesDAO().getAllPetSpecies();
        for (PetSpecies species : speciesList) {
            cbPetSpecies.addItem(species.id + "_" + species.getSpeciesName());
        }

        cbPetSpecies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbPets.removeAllItems();
                int speciesId = Integer.parseInt(cbPetSpecies.getSelectedItem().toString().split("_")[0]);
                ArrayList<Pet> pets = new PetDAO().getAllPetsBySpeiciesNotSaled(speciesId);
                for (Pet pet : pets) {
                    cbPets.addItem(pet);
                }
            }
        });

        JButton btnConfirm = new JButton("Confirm");
        final Pet[] selectedPet = {null};
        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPet[0] = (Pet) cbPets.getSelectedItem();
                dialog.dispose();
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(lblSpecies, gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialog.add(cbPetSpecies, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(lblPet, gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialog.add(cbPets, gbc);

        gbc.gridx = 1; gbc.gridy = 2; dialog.add(btnConfirm, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return selectedPet[0];
    }

    private void confirmOrder() {
    	if (selectedPets == null || selectedPets.size() == 0) return;
        // Tạo hộp thoại nhập thông tin khách hàng
        JPanel customerPanel = new JPanel(new GridLayout(3, 3));

        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JButton btnCheck = new JButton("Check");

        customerPanel.add(new JLabel("Name:"));
        customerPanel.add(txtName);
        customerPanel.add(new JLabel("Phone:"));
        customerPanel.add(txtPhone);
        customerPanel.add(new JLabel("")); // Dòng trống để cân bằng layout
        customerPanel.add(btnCheck);

        // Sự kiện cho nút Check
        btnCheck.addActionListener(e -> {
            String phone = txtPhone.getText().trim();
            if (!phone.isEmpty()) {
                // Tìm kiếm khách hàng dựa trên số điện thoại
                Customer customer = new CustomerDAO().getCustomerByPhone(phone);
                if (customer != null) {
                    txtName.setText(customer.getName()); // Điền tên vào ô nhập tên
                } else {
                    JOptionPane.showMessageDialog(this, "Customer not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                    txtName.setText(""); // Nếu không tìm thấy, xóa tên trong ô name
                }
            } else {
                JOptionPane.showMessageDialog(this, "Phone number is empty. Please enter a valid phone number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        int option = JOptionPane.showConfirmDialog(this, customerPanel, "Enter Customer Information", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name or Phone is empty. Customer ID will be set to 1 - GUEST.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                processOrder(1); // Sử dụng customer id = 1
            } else {
                // Kiểm tra số điện thoại có hợp lệ không (ví dụ, kiểm tra định dạng số điện thoại)
                String checkPhone = HelperFunc.isValidPhone(phone);
                if (checkPhone != "OK") {
                    JOptionPane.showMessageDialog(this, checkPhone + ". Customer ID will be set to 0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    processOrder(1); // Sử dụng customer id = 0
                } else {
                    // Tìm kiếm khách hàng dựa trên số điện thoại
                    int customerId = PetDBManager.findOrCreateCustomer(name, phone);
                    processOrder(customerId);
                }
            }
        }
    }



    private void processOrder(int customerId) {
        // Xử lý đơn hàng với customerId đã tìm hoặc tạo mới
        Object[] result = PetDBManager.confirmSellPet(selectedPets, customerId, 5);
        boolean isSuccess = (boolean) result[0];
        String invoiceFilePath = (String) result[1];

        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Order confirmed! Total: $" + HelperFunc.formatCurrency(totalAmount) + "\n Detail: " + invoiceFilePath);
            clearSelectedPets();
        } else {
            JOptionPane.showMessageDialog(this, "Something went wrong!!");
        }
    }

    // Thêm phương thức Clear
    private void clearSelectedPets() {
        selectedPets.clear(); // Xóa hết các thú cưng đã chọn
        petListModel.clear(); // Xóa danh sách hiển thị
        totalAmount = 0; // Đặt lại tổng tiền
        lblTotalAmount.setText("Total Amount: $0.0"); // Cập nhật lại nhãn
    }
}

