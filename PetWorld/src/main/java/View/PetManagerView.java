package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.View;

import ViewModel.PetCard;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import DAO.PetDAO;
import DAO.PetSpeciesDAO;
import DBManager.PetDBManager;
import Helper.Debug;
import Helper.HelperFunc;
import Helper.SessionManager;
import Model.Pet;
import Model.PetSpecies;

public class PetManagerView extends JFrame {
    private JPanel mainPanel;
    JSplitPane splitPane;
    JPanel menuPanel;
    
    private static final String DOWNLOAD_HISTORY = "DownloadHistory_Pet.json";
    private static final String IMAGE_DIR = "pet_images/";
    private ArrayList<PetCard> petList;
    private ArrayList<PetCard> petListFull;
    private int currentPage = 0;
    private final int petsPerPage = 9;

    private JComboBox<String> speciesFilter;
    private JComboBox<String> colorFilter;
    private JComboBox<String> weightFilter;
    private JTextField searchField;
    private JPanel paginationPanel;

    public PetManagerView() {
        setTitle("Pet Manager");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200); // Move filter and Pet display to the right
        add(splitPane, BorderLayout.CENTER);

        this.menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(60, 63, 65));
        menuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.DARK_GRAY));

        JButton btnHome = createStyledButton("Home", "./Assets/home.png");
        menuPanel.add(btnHome);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnPetManager = createStyledButton("Pet Manager", "./Assets/pets.png");
        JButton btnPetDetail = createStyledButton("Selected Pet Species", "./Assets/pet_2.png");
        JButton btnPetImport = createStyledButton("Pet Import", "./Assets/add_pet.png");
        JButton btnSellPet = createStyledButton("Sell Pet", "./Assets/sell.png");
        JButton btnCustomer = createStyledButton("Customer", "./Assets/customer.png");
        JButton btnOrder = createStyledButton("Order", "./Assets/order.png");
        JButton btnDashboard = createStyledButton("Dashboard", "./Assets/dashboard.png");
        JButton btnAccount = createStyledButton("Account", "./Assets/user.png");
        JButton btnLogout = createStyledButton("Logout(" + SessionManager.crrAccount.username +")", "./Assets/logout.png");
        
        ArrayList<JButton> menuButtons = new ArrayList<JButton>();
        menuButtons.add(btnPetManager);
        menuButtons.add(btnPetDetail);
        menuButtons.add(btnPetImport);
        menuButtons.add(btnSellPet);
        menuButtons.add(btnCustomer);
        menuButtons.add(btnOrder);
        menuButtons.add(btnDashboard);
        if (SessionManager.isAdmin)
        	menuButtons.add(btnAccount);
        
        menuButtons.add(btnLogout);
        
        for (int i = 0 ; i < menuButtons.size(); i++)
        {
        	addHighlightAction(menuButtons.get(i));
        }
        
        HelperFunc.AddButtonToPanel(menuPanel, menuButtons);
        
        
        menuPanel.add(Box.createVerticalGlue());
        
        ViewPetSpeciesPanel();
        setVisible(true);
        
        btnPetManager.addActionListener(e->{
        	ViewPetSpeciesPanel();
        });
        
        btnPetDetail.addActionListener(e->{
        	LoadPetInSelectedSpec(selectedSpec);
        });
        btnPetImport.addActionListener(e->{
        	LoadImportView();
        });
        btnSellPet.addActionListener(e->{
        	LoadSellView();
        });
        btnDashboard.addActionListener(e->{
        	LoadDashboardView();
        });
        btnAccount.addActionListener(e->{
        	LoadAccountPanel();
        });
        btnCustomer.addActionListener(e->{
        	LoadCustomerPanel();
        });
        btnOrder.addActionListener(e->{
        	LoadOrderPanel();
        });
        btnLogout.addActionListener(e->{
        	this.hide();
        	SessionManager.homepage = null;
        	SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginPage().setVisible(true);
                }
            });
        });
    }
    
    JButton selectedButton = null;
    private void addHighlightAction(JButton button) {
        button.addActionListener(e -> {
        	 if (selectedButton != null) {
                 selectedButton.setBorder(null);  // Bỏ highlight nút cũ
             }
             button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));  // Highlight nút mới
             selectedButton = button;
        });
    }
    
    private int selectedSpec = 0;
    
    private void LoadPetInSelectedSpec(int selectedSpec)
    {
    	Debug.Log("LoadPetInSpec: " + selectedSpec);
    	if (selectedSpec == 0) return;
    	petListFull = loadPetInSpeiciesData(selectedSpec, SessionManager.isAdmin);
        petList = HelperFunc.CloneList(petListFull);
        updatePagination();
        changePage(0);
    }
    
    private void LoadImportView()
    {
    	Debug.Log("Import/Export view");
    	JPanel rightPanel = new AddPetPanel();
        splitPane.setRightComponent(rightPanel);
    }
    
    SellPetPanel sellPetPanel = new SellPetPanel();
    private void LoadSellView()
    {
    	Debug.Log("Sell view");
    	JPanel rightPanel = sellPetPanel;
        splitPane.setRightComponent(rightPanel);
    }
    
    DashboardPanel dashboardPanel = new DashboardPanel();
    private void LoadDashboardView()
    {
    	Debug.Log("Dashboard view");
    	JPanel rightPanel = dashboardPanel;
        splitPane.setRightComponent(rightPanel);
    }
    
    private void LoadAccountPanel()
    {
    	Debug.Log("Account view");
    	JPanel rightPanel = new AccountManagementPanel();
        splitPane.setRightComponent(rightPanel);
    }
    private void LoadCustomerPanel()
    {
    	Debug.Log("Customer view");
    	JPanel rightPanel = new CustomerManagementPanel();
        splitPane.setRightComponent(rightPanel);
    }
    private void LoadOrderPanel()
    {
    	Debug.Log("Customer view");
    	JPanel rightPanel = new OrderManagementPanel();
        splitPane.setRightComponent(rightPanel);
    }
    
    private JPanel ViewPetSpeciesPanel()
    {
    	this.mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 3, 20, 20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel filterPanel = createFilterPanel();
        paginationPanel = new JPanel();
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(paginationPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(menuPanel);
        splitPane.setRightComponent(rightPanel);

       
        petListFull = loadPetData();
        petList = HelperFunc.CloneList(petListFull);
        loadPetCards(petList);
        updatePagination();
        
        return this.mainPanel;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        speciesFilter = new JComboBox<>(new String[] { "Tất cả", "Loài 1", "Loài 2", "Loài 3" });
        colorFilter = new JComboBox<>(new String[] { "Tất cả", "Đỏ", "Xanh", "Vàng" });
        weightFilter = new JComboBox<>(new String[] { "Tất cả", "Nhẹ", "Trung bình", "Nặng" });
        searchField = new JTextField(20);

        JButton filterButton = new JButton("Lọc");
        filterButton.addActionListener(e -> applyFilters());

        filterPanel.add(new JLabel("Loài:"));
        filterPanel.add(speciesFilter);
        filterPanel.add(new JLabel("Màu sắc:"));
        filterPanel.add(colorFilter);
        filterPanel.add(new JLabel("Khối lượng:"));
        filterPanel.add(weightFilter);
        filterPanel.add(new JLabel("Tìm kiếm:"));
        filterPanel.add(searchField);
        filterPanel.add(filterButton);

        return filterPanel;
    }

    private void applyFilters() {
        ArrayList<PetCard> filteredPets = new ArrayList<>(petListFull);

        String searchQuery = searchField.getText().toLowerCase();
        String selectedSpecies = (String) speciesFilter.getSelectedItem();
        String selectedColor = (String) colorFilter.getSelectedItem();
        String selectedWeight = (String) weightFilter.getSelectedItem();

        // Lọc theo từ khóa tìm kiếm
        if (!searchQuery.isEmpty()) {
            filteredPets.removeIf(pet -> !pet.name.toLowerCase().contains(searchQuery));
        }

        // Lọc theo loài
        if (!selectedSpecies.equals("Tất cả")) {
            filteredPets.removeIf(pet -> !pet.species.equals(selectedSpecies));
        }

        // Lọc theo màu sắc
        if (!selectedColor.equals("Tất cả")) {
            filteredPets.removeIf(pet -> !pet.color.equals(selectedColor));
        }

        // Lọc theo khối lượng
//        if (!selectedWeight.equals("Tất cả")) {
//            filteredPets.removeIf(pet -> !pet.weight.equals(selectedWeight));
//        }
        
        this.petList = filteredPets;
        loadPetCards(this.petList);
        updatePagination();
    }

    private void updatePagination() {
        paginationPanel.removeAll();
        int totalPages = (int) Math.ceil((double) petList.size() / petsPerPage);

        for (int i = 0; i < totalPages; i++) {
            int pageIndex = i;
            JButton pageButton = new JButton(String.valueOf(i + 1));
            pageButton.addActionListener(e -> changePage(pageIndex));
            pageButton.setBackground(new Color(0, 123, 255));
            pageButton.setForeground(Color.WHITE);
            pageButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            paginationPanel.add(pageButton);
        }
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private void changePage(int pageIndex) {
        currentPage = pageIndex;
        loadPetCards(petList);
    }

    private void loadPetCards(ArrayList<PetCard> pets) {
    	Debug.Log("Load pet cards: " + pets.size());
        mainPanel.removeAll();

        int startIndex = currentPage * petsPerPage;
        int endIndex = Math.min(startIndex + petsPerPage, pets.size());

        for (int i = startIndex; i < endIndex; i++) {
        	PetCard pc = pets.get(i);
            JPanel petCard = createPetCard(pc);
            mainPanel.add(petCard);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createPetCard(PetCard pet) {
    	boolean isPet = pet.mode.equals("PET");
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(200, 250));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        if (pet.mode.equals("PET") && pet.pet.state.equals("SALED")) {
            card.setBackground(new Color(144, 238, 144));  // Màu xanh lá nhạt
        }
        JLabel petImage = new JLabel();
        try {
            String localPath = getCachedImage(pet.imageUrl);
            Image image = ImageIO.read(new File(localPath)).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            petImage.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            petImage.setText("Image not found");
        }

        JLabel petNameLabel = new JLabel(pet.name);
        JLabel petQuantityLabel = new JLabel("Số lượng hiện có: " + pet.quantity);
        
        JLabel petPriceLabel = null;
        if (pet.mode == "SPEC")
        {
        	petPriceLabel = new JLabel("Giá: " + HelperFunc.formatCurrency(pet.minPrice) + " - " + HelperFunc.formatCurrency(pet.maxPrice));
        }
        else
        {
        	petPriceLabel = new JLabel("Giá: " + HelperFunc.formatCurrency(pet.petPrice));
        }
        
        JLabel petColorLabel = new JLabel("Màu sắc: " + pet.color);  // Thêm thông tin màu sắc
        JLabel petWeightLabel = new JLabel("Cân nặng: " + pet.weight + "(KG)");  // Thêm thông tin màu sắc
        String tempText = pet.mode.equals("SPEC") ? "Tuổi thọ trung bình" : "Tuổi";
        JLabel petAgeLabel = new JLabel( tempText + ": " + pet.age);  // Thêm thông tin màu sắc

        petNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        petNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petQuantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Căn chỉnh label giá
        petColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Căn chỉnh label màu sắc
        petWeightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petAgeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petImage.setAlignmentX(Component.CENTER_ALIGNMENT);

       ArrayList<JButton> buttons = new ArrayList<JButton>();
       if (pet.mode == "SPEC")
    	   buttons = GenerateButtonForSPEC(pet);
       else
    	   buttons = GenerateButtonForPET(pet);

       

        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Center align the buttons
        HelperFunc.AddButtonToPanel(buttonPanel, buttons);

        card.add(Box.createVerticalGlue());
        card.add(petImage);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(petNameLabel);
        if (!isPet)
        	card.add(petQuantityLabel);
        card.add(petPriceLabel);  // Thêm label giá vào card
        if (isPet)
        	card.add(petColorLabel);  // Thêm label màu sắc vào card
        if (isPet)
        	card.add(petWeightLabel);
        card.add(petAgeLabel);
        card.add(buttonPanel);  // Add the button panel
        card.add(Box.createVerticalGlue());

        return card;
    }
    
    private ArrayList<JButton> GenerateButtonForSPEC(PetCard pet)
    {
        // Create View button
        JButton viewButton = new JButton("View");
        viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewButton.setFont(new Font("Arial", Font.BOLD, 12));  // Smaller font size
        viewButton.setBackground(new Color(40, 167, 69)); // Set background color (green)
        viewButton.setForeground(Color.WHITE);  // Set text color (white)
        viewButton.setBorder(BorderFactory.createLineBorder(new Color(28, 145, 41), 2)); // Border
        viewButton.setFocusPainted(false);
        viewButton.setPreferredSize(new Dimension(70, 25));  // Smaller button size
        viewButton.addActionListener(e -> 
        {
            System.out.println("View Card:");
            this.selectedSpec = pet.specID;
            LoadPetInSelectedSpec(this.selectedSpec);
        });

        // Create Edit button
        JButton editButton = new JButton("Edit");
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.setFont(new Font("Arial", Font.BOLD, 12));  // Smaller font size
        editButton.setBackground(new Color(0, 123, 255)); // Set background color (blue)
        editButton.setForeground(Color.WHITE);  // Set text color (white)
        editButton.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 179), 2)); // Border
        editButton.setFocusPainted(false);
        editButton.setPreferredSize(new Dimension(70, 25));  // Smaller button size
        editButton.addActionListener(e -> {
            openEditPetSpeciesDialog(pet);  // Open the edit dialog for the selected pet species
        });

        // Create Delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));  // Smaller font size
        deleteButton.setBackground(new Color(220, 53, 69)); // Set background color (red)
        deleteButton.setForeground(Color.WHITE);  // Set text color (white)
        deleteButton.setBorder(BorderFactory.createLineBorder(new Color(185, 41, 56), 2)); // Border
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(70, 25));  // Smaller button size
        deleteButton.addActionListener(e -> System.out.println("Delete Card"));

        ArrayList<JButton> rs = new ArrayList<JButton>();
        rs.add(viewButton);
        if (SessionManager.isAdmin)
        {
            rs.add(editButton);
            rs.add(deleteButton);
        }
        return rs;
    }

    private void openEditPetSpeciesDialog(PetCard pet) {
        PetSpecies species = new PetSpeciesDAO().getPetSpeciesById(pet.specID);  // Fetch current species data from DB
        
        JTextField txtID = new JTextField(String.valueOf(species.id));
        txtID.setEditable(false);  // Set ID to read-only
        txtID.setPreferredSize(new Dimension(150, 25));  // Set preferred size for ID

        JTextField txtSpeciesName = new JTextField(species.getSpeciesName());
        txtSpeciesName.setPreferredSize(new Dimension(150, 25));  // Set preferred size for species name

        JTextField txtMinPrice = new JTextField(String.valueOf(species.getAvgMinPrice()));
        txtMinPrice.setPreferredSize(new Dimension(150, 25));  // Set preferred size for min price

        JTextField txtMaxPrice = new JTextField(String.valueOf(species.getAvgMaxPrice()));
        txtMaxPrice.setPreferredSize(new Dimension(150, 25));  // Set preferred size for max price

        JTextField txtAvgWeight = new JTextField(String.valueOf(species.getAvgWeight()));
        txtAvgWeight.setPreferredSize(new Dimension(150, 25));  // Set preferred size for avg weight

        JTextField txtMaxAge = new JTextField(String.valueOf(species.getAvgMaxAge()));
        txtMaxAge.setPreferredSize(new Dimension(150, 25));  // Set preferred size for max age

        JTextField txtImageUrl = new JTextField(species.imageUrl);
        txtImageUrl.setPreferredSize(new Dimension(150, 25));  // Set preferred size for image URL

        JTextArea txtDescription = new JTextArea(species.des, 5, 20);
        txtDescription.setPreferredSize(new Dimension(150, 80));  // Set preferred size for description
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setPreferredSize(new Dimension(150, 80));  // Set preferred size for the scroll pane

        // Create a panel to input/edit the information
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));  // Add spacing between components
        panel.setPreferredSize(new Dimension(600, 400));
        panel.add(new JLabel("ID:"));
        panel.add(txtID);  // ID is read-only
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
        panel.add(scrollPane);

        // Show dialog for editing
        int option = JOptionPane.showConfirmDialog(null, panel, "Edit Pet Species", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            // Get values from the input fields
            String speciesName = txtSpeciesName.getText();
            float minPrice = Float.parseFloat(txtMinPrice.getText());
            float maxPrice = Float.parseFloat(txtMaxPrice.getText());
            float avgWeight = Float.parseFloat(txtAvgWeight.getText());
            int maxAge = Integer.parseInt(txtMaxAge.getText());
            String imageUrl = txtImageUrl.getText();
            String description = txtDescription.getText();
            
            Debug.Log("update url: " + species.id);

            // Create a new PetSpecies object with the updated values
            PetSpecies updatedSpecies = new PetSpecies(species.id, speciesName, minPrice, maxPrice, avgWeight, maxAge, imageUrl, description);

            // Update the species in the database
            new PetSpeciesDAO().updatePetSpecies(updatedSpecies);

            JOptionPane.showMessageDialog(null, "Pet Species updated successfully!");
        }
    }


    
    private ArrayList<JButton> GenerateButtonForPET(PetCard pet)
    {
    	 // Create View button with a more attractive design and smaller size
        JButton viewButton = new JButton("Sell");
        viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewButton.setFont(new Font("Arial", Font.BOLD, 12));  // Smaller font size
        viewButton.setBackground(new Color(40, 167, 69)); // Set background color (green)
        viewButton.setForeground(Color.WHITE);  // Set text color (white)
        viewButton.setBorder(BorderFactory.createLineBorder(new Color(28, 145, 41), 2)); // Border
        viewButton.setFocusPainted(false);
        viewButton.setPreferredSize(new Dimension(70, 25));  // Smaller button size
        viewButton.addActionListener(e -> 
        {
        	System.out.println("Sell Pet: " + pet.name);
        	this.sellPetPanel.addPetToOrder(pet.pet);
        	pet.state = "IN_ORDER";
        });

        // Create Edit button with a more attractive design and smaller size
        JButton editButton = new JButton("Edit");
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.setFont(new Font("Arial", Font.BOLD, 12));  // Smaller font size
        editButton.setBackground(new Color(0, 123, 255)); // Set background color (blue)
        editButton.setForeground(Color.WHITE);  // Set text color (white)
        editButton.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 179), 2)); // Border
        editButton.setFocusPainted(false);
        editButton.setPreferredSize(new Dimension(70, 25));  // Smaller button size
        editButton.addActionListener(e -> System.out.println("Edit Card:"));

        // Create Delete button with a more attractive design and smaller size
        JButton deleteButton = new JButton("Delete");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));  // Smaller font size
        deleteButton.setBackground(new Color(220, 53, 69)); // Set background color (red)
        deleteButton.setForeground(Color.WHITE);  // Set text color (white)
        deleteButton.setBorder(BorderFactory.createLineBorder(new Color(185, 41, 56), 2)); // Border
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(70, 25));  // Smaller button size
        deleteButton.addActionListener(e -> System.out.println("Delete Card"));
        
        ArrayList<JButton> rs = new ArrayList<JButton>();
        rs.add(viewButton);
//        if (SessionManager.isAdmin)
//        {
//            rs.add(editButton);
//            rs.add(deleteButton);
//        }

        return rs;
    }

    private String getCachedImage(String imageUrl) throws IOException {
    	if (imageUrl == null) return "";
    	if (!imageUrl.startsWith("http") && !imageUrl.startsWith("www"))
    	{
    		return imageUrl;
    	}
        File dir = new File(IMAGE_DIR);
        if (!dir.exists()) dir.mkdirs();

        JSONObject history = loadDownloadHistory();
        if (history.containsKey(imageUrl)) {
            return (String) history.get(imageUrl);
        }

        String fileName = IMAGE_DIR + UUID.randomUUID() + ".jpg";
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, new File(fileName).toPath());
            history.put(imageUrl, fileName);
            saveDownloadHistory(history);
        }
        return fileName;
    }

    private JSONObject loadDownloadHistory() {
        try (FileReader reader = new FileReader(DOWNLOAD_HISTORY)) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(reader);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private void saveDownloadHistory(JSONObject history) {
        try (FileWriter writer = new FileWriter(DOWNLOAD_HISTORY)) {
            writer.write(history.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text, new ImageIcon(iconPath));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 50));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(75, 110, 175));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }
    
    private ArrayList<PetCard> loadPetData() {
    	ArrayList<PetCard> petList = new ArrayList<>();
        ArrayList<PetSpecies> data = PetDBManager.getPetSpeciesWithUnsoldAndTotalPetsCount();
    	int n = data.size();
        for (int i = 0; i < n; i++)
    	{
        	PetSpecies ps = data.get(i);
        	if (ps.des == null) ps.des = "";
        	if (ps.des.startsWith("TEMP")) continue;
    		petList.add(ps.toPetCard());
    	}
    	
        petList.sort(Comparator.comparing(PetCard::getName));
        
        return petList;
    }
    
    private ArrayList<PetCard> loadPetInSpeiciesData(int speciesID, boolean isAdmin) {
    	ArrayList<PetCard> petDetailList = new ArrayList<>();
        ArrayList<Pet> data = new ArrayList<Pet>();
        if (isAdmin)
        	data = new PetDAO().getAllPetsBySpeicies(speciesID);
        else
        	data = new PetDAO().getAllPetsBySpeiciesNotSaled(speciesID);
    	int n = data.size();
        for (int i = 0; i < n; i++)
    	{
        	Debug.Log("Pet: " + data.get(i).petName);
        	petDetailList.add(data.get(i).toPetCard());
    	}
    	
        petDetailList.sort(Comparator.comparing(PetCard::getName));
        
        return petDetailList;
    }
}


