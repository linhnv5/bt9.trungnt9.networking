package topica.linh.trungnt9.networking.bai1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility to help read and write date follow the fomat dd/MM/yyyy
 * @author ljnk975
 *
 */
public class DateUtil {

	///-> Singleton pattern
	private static class DateUtilHelper {
		static DateUtil instance = new DateUtil(); // instance of util
	}

	private DateUtil() {}

	
	public static DateUtil gI() {
		return DateUtilHelper.instance;
	}

	/**
	 * Fommater dd/MM/yyyy
	 */
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Convert date object to string format dd/MM/yyyy
	 * @param d Date object to covert
	 * @return String of date follow the format dd/MM/yyyy
	 */
	public String format(Date d) {
		return formatter.format(d);
	}

	/**
	 * Covert a string to java date object
	 * String input format is dd/MM/yyyy
	 * @param s String input
	 * @return  Date object hold info from string
	 * @throws ParseException if string input not follow the format dd/MM/yyyy
	 */
	public Date parse(String s) throws ParseException {
		return formatter.parse(s);
	}

}
