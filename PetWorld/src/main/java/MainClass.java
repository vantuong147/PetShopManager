import java.util.ArrayList;

import javax.swing.SwingUtilities;

import DAO.AccountDAO;
import Helper.Debug;
import Model.Account;
import View.LoginPage;
import View.PetManagerView;

public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Debug.Log("Hello world");
		// Mở giao diện đăng nhập
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginPage().setVisible(true);
            }
        });
	}

}
