package topica.linh.trungnt9.networking.bai2.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Server manager hold username-phonenumber map
 * @author ljnk975
 *
 */
final class Server {

	// singleton
	private static class ServerInstanceHelper {
		public static Server instance = new Server();
	}

	/**
	 * Get instance of server
	 * @return server instance
	 */
	public static final Server gI() {
		return ServerInstanceHelper.instance;
	}

	/**
	 * Default constructor
	 */
	private Server() {
		this.mapPhoneUser = new HashMap<String, String>();
	}

	/**
	 * Map to hold user by phone number
	 */
	private Map<String, String> mapPhoneUser;

	/**
	 * Get name by phone number pushed by insert key
	 * @param phoneNumber phone number
	 * @return name of phoneNumber
	 */
	public synchronized final String getNameByPhoneNumber(String phoneNumber) {
		return mapPhoneUser.get(phoneNumber);
	}

	/**
	 * Put username for phoneNumber
	 * @param phoneNumber phone number
	 * @param user name of phone number's user
	 */
	public synchronized final void addUser(String phoneNumber, String user) {
		this.mapPhoneUser.put(phoneNumber, user);
	}

}
