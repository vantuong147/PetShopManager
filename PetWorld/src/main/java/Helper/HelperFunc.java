package Helper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HelperFunc {
	public static <T> ArrayList<T> CloneList(ArrayList<T> lst)
	{
		ArrayList<T> result = new ArrayList<T>();
		int n = lst.size();
		for(int i = 0; i < n; i++)
		{
			result.add(lst.get(i));
		}
		return result;
	}
	
	public static String formatCurrency(double amount) {
	    // Định dạng số theo kiểu tiền tệ với đơn vị VND
	    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
	    return formatter.format(amount);
	}
}
