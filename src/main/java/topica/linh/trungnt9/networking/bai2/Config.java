package topica.linh.trungnt9.networking.bai2;

/**
 * Config class, contain some config variable
 * @author ljnk975
 *
 */
public class Config {

	/**
	 * Phone number regex, 098xxxxxxxx, first x is [2-9] other x [0-9]
	 */
	public static final String PHONE_NUMBER_REGEX = "^098[2-9]\\d{6}$";
	
	/**
	 * Authen key to enter ready state
	 */
	public static final String KEY_AUTHEN = "topica";

	/**
	 * Server listen port
	 */
	public static final int port = 9669;
}
