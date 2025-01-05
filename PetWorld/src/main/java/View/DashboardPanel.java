package View;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;

import DAO.OrderDAO;
import DAO.OrderDetailDAO;
import DAO.PetDAO;
import DAO.PetSpeciesDAO;
import Model.Order;
import Model.OrderDetail;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;

public class DashboardPanel extends JPanel {

    private JPanel barChartPanel;
    private JPanel pieChartPanel;
    private JSpinner yearSpinner;
    private JSpinner monthSpinner;
    private JSpinner yearRevenueSpinner;
    private JSpinner monthRevenueSpinner;

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10)); // Sử dụng BorderLayout với khoảng cách giữa các phần tử

        // Panel cho thông số thống kê số lượng pet bán ra (Góc trái trên)
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        // Biểu đồ cột cho số lượng pet bán ra (Góc phải trên)
        barChartPanel = createBarChartPanel_Year();
        add(barChartPanel, BorderLayout.CENTER);

        // Panel cho thông số thống kê doanh thu (Góc trái dưới)
        JPanel revenueStatsPanel = createRevenueStatsPanel();
        add(revenueStatsPanel, BorderLayout.SOUTH);

        // Biểu đồ tròn cho doanh thu (Góc phải dưới)
        pieChartPanel = createPieChartPanel_Year();
        add(pieChartPanel, BorderLayout.EAST);
    }

    // Hàm tạo Panel thông số thống kê số lượng pet bán ra
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Pet Sales Statistics"));

        // Năm (Spinner)
        yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2000, 2100, 1));
        statsPanel.add(createLabeledField("Select Year:", yearSpinner));

        // Tháng (Spinner)
        monthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        statsPanel.add(createLabeledField("Select Month:", monthSpinner));

        // Panel cho 2 button nằm ngang
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Button thống kê theo năm
        JButton yearButton = new JButton("Show Yearly Data");
        yearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Xử lý thống kê theo năm
                int year = (int) yearSpinner.getValue();
                showYearlyData(year);
            }
        });
        buttonPanel.add(yearButton);

        // Button thống kê theo tháng
        JButton monthButton = new JButton("Show Monthly Data");
        monthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Xử lý thống kê theo tháng
                int month = (int) monthSpinner.getValue();
                showMonthlyData(month);
            }
        });
        buttonPanel.add(monthButton);

        statsPanel.add(buttonPanel);

        return statsPanel;
    }

    // Hàm tạo Panel thông số thống kê doanh thu
    private JPanel createRevenueStatsPanel() {
        JPanel revenueStatsPanel = new JPanel();
        revenueStatsPanel.setLayout(new BoxLayout(revenueStatsPanel, BoxLayout.Y_AXIS));
        revenueStatsPanel.setBorder(BorderFactory.createTitledBorder("Revenue Statistics"));

        // Năm cho doanh thu
        yearRevenueSpinner = new JSpinner(new SpinnerNumberModel(2025, 2000, 2100, 1));
        revenueStatsPanel.add(createLabeledField("Select Year for Revenue:", yearRevenueSpinner));

        // Tháng cho doanh thu
        monthRevenueSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        revenueStatsPanel.add(createLabeledField("Select Month for Revenue:", monthRevenueSpinner));

        // Panel cho 2 button nằm ngang
        JPanel revenueButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Button thống kê doanh thu theo năm
        JButton revenueYearButton = new JButton("Show Yearly Revenue");
        revenueYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Xử lý thống kê doanh thu theo năm
                int year = (int) yearRevenueSpinner.getValue();
                showYearlyRevenue(year);
            }
        });
        revenueButtonPanel.add(revenueYearButton);

        // Button thống kê doanh thu theo tháng
        JButton revenueMonthButton = new JButton("Show Monthly Revenue");
        revenueMonthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Xử lý thống kê doanh thu theo tháng
                int month = (int) monthRevenueSpinner.getValue();
                showMonthlyRevenue(month);
            }
        });
        revenueButtonPanel.add(revenueMonthButton);

        revenueStatsPanel.add(revenueButtonPanel);

        return revenueStatsPanel;
    }

    // Hàm tạo biểu đồ cột (Bar Chart) thể hiện số lượng pet bán ra
    private JPanel createBarChartPanel_Year() {
        // Lấy danh sách đơn hàng từ OrderDAO
        OrderDAO orderDAO = new OrderDAO();
        List<Order> orders = orderDAO.getAllOrders();
        
        // Tạo dataset cho biểu đồ cột
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> salesData = new LinkedHashMap<>();

        // Khởi tạo dữ liệu cho đủ 12 tháng
        String[] months = new DateFormatSymbols().getShortMonths();
        for (int i = 0; i < 12; i++) {
            salesData.put(months[i], 0);
        }

        int year = (int) yearSpinner.getValue();
        
        // Đếm số lượng đơn hàng theo tháng của năm được chọn
        for (Order order : orders) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.orderDate);
            
            int orderYear = cal.get(Calendar.YEAR);
            if (orderYear == year) {  // Chỉ đếm đơn hàng của năm được chọn
                int month = cal.get(Calendar.MONTH);  // 0 - 11
                salesData.put(months[month], salesData.getOrDefault(months[month], 0) + 1);
            }
        }
        
        // Đưa dữ liệu vào dataset
        for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
            dataset.addValue(entry.getValue(), "Sales", entry.getKey());
        }

        // Tạo biểu đồ cột
        JFreeChart barChart = ChartFactory.createBarChart(
                "Number of Pets Sold (" + year + ")", "Month", "Sales",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createBarChartPanel_Month() {
        // Lấy danh sách đơn hàng từ OrderDAO
        OrderDAO orderDAO = new OrderDAO();
        List<Order> orders = orderDAO.getAllOrders();
        
        // Tạo dataset cho biểu đồ cột
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<Integer, Integer> salesData = new LinkedHashMap<>();

        // Khởi tạo dữ liệu cho đủ 31 ngày
        for (int i = 1; i <= 31; i++) {
            salesData.put(i, 0);
        }

        int year = (int) yearSpinner.getValue();
        int month = (int) monthSpinner.getValue();  // Lấy giá trị tháng từ spinner

        // Đếm số lượng đơn hàng theo ngày của tháng được chọn
        for (Order order : orders) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.orderDate);
            
            int orderYear = cal.get(Calendar.YEAR);
            int orderMonth = cal.get(Calendar.MONTH) + 1;  // Calendar.MONTH từ 0 - 11
            int day = cal.get(Calendar.DAY_OF_MONTH);

            if (orderYear == year && orderMonth == month) {
                salesData.put(day, salesData.get(day) + 1);
            }
        }
        
        // Đưa dữ liệu vào dataset
        for (Map.Entry<Integer, Integer> entry : salesData.entrySet()) {
            dataset.addValue(entry.getValue(), "Sales", entry.getKey().toString());
        }

        // Tạo biểu đồ cột
        JFreeChart barChart = ChartFactory.createBarChart(
                "Number of Pets Sold - " + month + "/" + year, "Day", "Sales",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    // Hàm tạo biểu đồ tròn (Pie Chart) thể hiện doanh thu
    private JPanel createPieChartPanel_Year() {
        // Lấy danh sách đơn hàng từ OrderDAO
        OrderDAO orderDAO = new OrderDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        PetDAO petDAO = new PetDAO();
        PetSpeciesDAO petSpeciesDAO = new PetSpeciesDAO();
        List<Order> orders = orderDAO.getAllOrders();
        
        // Tạo dataset cho biểu đồ tròn
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Integer> petTypeSales = new LinkedHashMap<>();
        
        // Lấy năm từ yearSpinner
        int year = (int) yearRevenueSpinner.getValue();
        
        // Duyệt qua tất cả các đơn hàng và thống kê số lượng pet bán theo loại
        for (Order order : orders) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.orderDate);
            
            int orderYear = cal.get(Calendar.YEAR);
            if (orderYear == year) { // Chỉ thống kê đơn hàng trong năm được chọn
            	List<OrderDetail> lstOrderDetail = orderDetailDAO.getOrderDetailsByOrderId(order.id);
            	int petId = orderDetailDAO.getOrderDetailById(order.id).petId;
            	int typeId = petDAO.getPetById(petId).petSpeciesId;
            	String typeName = petSpeciesDAO.getPetSpeciesById(typeId).speciesName;
                
                // Tăng số lượng của loại pet trong danh sách
                petTypeSales.put(typeName, petTypeSales.getOrDefault(typeName, 0) + lstOrderDetail.size());
            }
        }
        
        // Đưa dữ liệu vào dataset
        for (Map.Entry<String, Integer> entry : petTypeSales.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }
        
        // Tạo biểu đồ tròn
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Pet Sales by Type (" + year + ")", pieDataset, true, true, false);
        
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createPieChartPanel_Month() {
        // Lấy danh sách đơn hàng từ OrderDAO
        OrderDAO orderDAO = new OrderDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        PetDAO petDAO = new PetDAO();
        PetSpeciesDAO petSpeciesDAO = new PetSpeciesDAO();
        List<Order> orders = orderDAO.getAllOrders();
        
        // Tạo dataset cho biểu đồ tròn
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Integer> petTypeSales = new LinkedHashMap<>();
        
        // Lấy năm từ yearSpinner
        int year = (int) yearRevenueSpinner.getValue();
        int month = (int) monthRevenueSpinner.getValue();
        
        // Duyệt qua tất cả các đơn hàng và thống kê số lượng pet bán theo loại
        for (Order order : orders) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.orderDate);
            
            int orderYear = cal.get(Calendar.YEAR);
            int orderMonth = cal.get(Calendar.MONTH) + 1;
            if (orderYear == year && orderMonth == month) { // Chỉ thống kê đơn hàng trong năm được chọn
            	List<OrderDetail> lstOrderDetail = orderDetailDAO.getOrderDetailsByOrderId(order.id);
            	int petId = orderDetailDAO.getOrderDetailById(order.id).petId;
            	int typeId = petDAO.getPetById(petId).petSpeciesId;
            	String typeName = petSpeciesDAO.getPetSpeciesById(typeId).speciesName;
                
                // Tăng số lượng của loại pet trong danh sách
                petTypeSales.put(typeName, petTypeSales.getOrDefault(typeName, 0) + lstOrderDetail.size());
            }
        }
        
        // Đưa dữ liệu vào dataset
        for (Map.Entry<String, Integer> entry : petTypeSales.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }
        
        // Tạo biểu đồ tròn
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Pet Sales by Type - " + month + "/" + year, pieDataset, true, true, false);
        
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    // Hàm giúp tạo các phần tử với JLabel và các field cho dữ liệu đầu vào
    private JPanel createLabeledField(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    // Xử lý thống kê theo năm
    private void showYearlyData(int year) {
        // Ví dụ đơn giản: Chỉ hiển thị thông tin năm và sẽ cập nhật biểu đồ cột theo năm này
        System.out.println("Displaying data for year: " + year);
        
        barChartPanel.removeAll();
        
        barChartPanel.add(createBarChartPanel_Year());
        
        revalidate();  // Làm mới giao diện
        repaint();
    }

    // Xử lý thống kê theo tháng
    private void showMonthlyData(int month) {
        // Ví dụ đơn giản: Chỉ hiển thị thông tin tháng và sẽ cập nhật biểu đồ cột theo tháng này
        System.out.println("Displaying data for month: " + month);
        
        barChartPanel.removeAll();
        
        barChartPanel.add(createBarChartPanel_Month());
        
        revalidate();  // Làm mới giao diện
        repaint();
    }

    // Xử lý thống kê doanh thu theo năm
    private void showYearlyRevenue(int year) {
        System.out.println("Displaying revenue data for year: " + year);
        // Cập nhật lại biểu đồ tròn với dữ liệu doanh thu theo năm
        
        pieChartPanel.removeAll();
        
        pieChartPanel.add(createPieChartPanel_Year());
        
        revalidate();  // Làm mới giao diện
        repaint();
    }

    // Xử lý thống kê doanh thu theo tháng
    private void showMonthlyRevenue(int month) {
        System.out.println("Displaying revenue data for month: " + month);
        // Cập nhật lại biểu đồ tròn với dữ liệu doanh thu theo tháng
        
        pieChartPanel.removeAll();
        
        pieChartPanel.add(createPieChartPanel_Month());
        
        revalidate();  // Làm mới giao diện
        repaint();
    }

    public static void main(String[] args) {
        // Tạo JFrame
        JFrame frame = new JFrame("Dashboard");
        DashboardPanel dashboardPanel = new DashboardPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(dashboardPanel);
        frame.setSize(1200, 800);
        frame.setVisible(true);
    }
}