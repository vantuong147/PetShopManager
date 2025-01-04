import java.util.ArrayList;

import javax.swing.SwingUtilities;

import DAO.AccountDAO;
import Helper.Debug;
import Model.Account;
import View.PetManagerView;

public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Debug.Log("Hello world");
		SwingUtilities.invokeLater(PetManagerView::new);
	}

}
