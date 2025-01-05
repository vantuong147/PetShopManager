package Helper;

import javax.swing.JFrame;

import Model.Account;

public class SessionManager {
	public static boolean isAdmin = false;
	
	public static Account crrAccount = null;
	public static JFrame homepage = null;
	
	public static void SetupAccount(Account acc)
	{
		crrAccount = acc;
		isAdmin = acc.accountType.equals("ADMIN");
	}
}
