package DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	private static final Boolean isUseWindowAuthentiation = false;
	private static final String URL_1 = "jdbc:sqlserver://localhost:1433;databaseName=PetManager;integratedSecurity=true;trustServerCertificate=true"; // Tin cậy chứng chỉ
	private static final String URL_2 = "jdbc:sqlserver://localhost:1433;databaseName=PetManager;encrypt=true;trustServerCertificate=true;"; // Chỉ định mã hóa và tin tưởng chứng chỉ
    private static final String USER = "sa"; // Thay thế bằng tên người dùng của bạn
    private static final String PASSWORD = "123456"; // Thay thế bằng mật khẩu của bạn

    private static DBConnection instance;
    
    public static Connection getConnection()
    {
    	Connection connection = null;
    	try
    	{
        	if (!isUseWindowAuthentiation)
        		connection = DriverManager.getConnection(URL_2, USER, PASSWORD);
        	else
        		connection = DriverManager.getConnection(URL_1);
        	System.out.println("Connected to the database successfully!");
        	return connection;
    	}
    	catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
