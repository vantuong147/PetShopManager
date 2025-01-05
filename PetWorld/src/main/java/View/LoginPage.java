package View;

import javax.swing.*;
import javax.swing.border.Border;

import DAO.AccountDAO;
import Helper.SessionManager;
import Model.Account;

import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {
    // Các thành phần giao diện
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginPage() {
        // Thiết lập cửa sổ đăng nhập
        setTitle("Login Page");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Đặt cửa sổ ở giữa màn hình

        // Thêm icon cho cửa sổ
     // Thêm icon cho cửa sổ
        setIconImage(new ImageIcon("Assets/login.png").getImage());


        // Tạo các thành phần giao diện
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10)); // Thêm khoảng cách giữa các thành phần
        panel.setBackground(new Color(255, 255, 255)); // Màu nền trắng

        // Label và các trường nhập liệu
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsername.setForeground(new Color(50, 50, 50));  // Màu chữ xám đậm

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(200, 20));  // Kích thước ô nhập liệu
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(new Color(50, 50, 50));

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(200, 20));  // Kích thước ô nhập liệu
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));

        // Nút Login
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(50, 150, 250));  // Màu nền xanh dương
        btnLogin.setForeground(Color.WHITE);  // Màu chữ trắng
        btnLogin.setFocusPainted(false);  // Loại bỏ viền focus khi nhấn
        btnLogin.setPreferredSize(new Dimension(100, 40));

        // Thêm các thành phần vào panel
        panel.add(lblUsername);
        panel.add(txtUsername);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(new JLabel());  // Thêm khoảng trống
        panel.add(btnLogin);

        // Thiết lập Border cho cửa sổ
        Border border = BorderFactory.createLineBorder(new Color(50, 150, 250), 2);  // Viền màu xanh dương
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Khoảng cách giữa viền và nội dung

        // Thêm panel vào JFrame
        add(panel);

        // Xử lý sự kiện nhấn nút Login
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                AccountDAO dao = new AccountDAO();
                Account acc = dao.checkLogin(username, password);
                if (acc != null) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    SessionManager.SetupAccount(acc);
                    openHomePage();
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Invalid Username or Password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Mở giao diện trang chủ (HomePage)
    private void openHomePage() {
        // Đóng cửa sổ đăng nhập hiện tại
        this.dispose();

        // Tạo và hiển thị trang chủ
        JFrame homePage = new PetManagerView();
        SessionManager.homepage = homePage;
    }
}
