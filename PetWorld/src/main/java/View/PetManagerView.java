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
import DBManager.PetDBManager;
import Helper.Debug;
import Helper.HelperFunc;
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

        JButton btnHome = createStyledButton("Home", "home-icon.png");
        menuPanel.add(btnHome);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnPetManager = createStyledButton("Pet Manager", "./Assets/pets.png");
        JButton btnPetDetail = createStyledButton("Selected Pet Species", "./Assets/pet_2.png");
        JButton btnPetImportExport = createStyledButton("Pet Import/Export", "./Assets/add_pet.png");
        JButton btnAccount = createStyledButton("Account", "./Assets/user.png");
        
        ArrayList<JButton> menuButtons = new ArrayList<JButton>();
        menuButtons.add(btnPetManager);
        menuButtons.add(btnPetDetail);
        menuButtons.add(btnPetImportExport);
        menuButtons.add(btnAccount);
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
        btnPetImportExport.addActionListener(e->{
        	LoadImportExportView();
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
    	petListFull = loadPetInSpeiciesData(selectedSpec);
        petList = HelperFunc.CloneList(petListFull);
        updatePagination();
        changePage(0);
    }
    
    private void LoadImportExportView()
    {
    	Debug.Log("Import/Export view");
    	JPanel rightPanel = new AddPetPanel();
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
            JPanel petCard = createPetCard(pets.get(i));
            mainPanel.add(petCard);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createPetCard(PetCard pet) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(200, 250));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel petImage = new JLabel();
        try {
            String localPath = getCachedImage(pet.imageUrl);
            Image image = ImageIO.read(new File(localPath)).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            petImage.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            petImage.setText("Image not found");
        }

        JLabel petNameLabel = new JLabel(pet.name);
        JLabel petQuantityLabel = new JLabel("Số lượng: " + pet.quantity);
        
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

        petNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        petNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petQuantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Căn chỉnh label giá
        petColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Căn chỉnh label màu sắc
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
        card.add(petQuantityLabel);
        card.add(petPriceLabel);  // Thêm label giá vào card
        card.add(petColorLabel);  // Thêm label màu sắc vào card
        card.add(buttonPanel);  // Add the button panel
        card.add(Box.createVerticalGlue());

        return card;
    }
    
    private ArrayList<JButton> GenerateButtonForSPEC(PetCard pet)
    {
    	 // Create View button with a more attractive design and smaller size
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
        rs.add(editButton);
        rs.add(deleteButton);
        return rs;
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
        rs.add(editButton);
        rs.add(deleteButton);
        return rs;
    }

    private String getCachedImage(String imageUrl) throws IOException {
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
    		petList.add(data.get(i).toPetCard());
    	}
    	
        petList.sort(Comparator.comparing(PetCard::getName));
        
        return petList;
    }
    
    private ArrayList<PetCard> loadPetInSpeiciesData(int speciesID) {
    	ArrayList<PetCard> petDetailList = new ArrayList<>();
        ArrayList<Pet> data = new PetDAO().getAllPetsBySpeicies(speciesID);
    	int n = data.size();
        for (int i = 0; i < n; i++)
    	{
        	Debug.Log("Pet: " + data.get(i).petName);
        	petDetailList.add(data.get(i).toPetCard());
    	}
    	
        petDetailList.sort(Comparator.comparing(PetCard::getName));
        
        return petDetailList;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PetManagerView::new);
    }
}


