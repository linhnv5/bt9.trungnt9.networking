package topica.linh.trungnt9.networking.bai2.network;

/**
 * Interface define a function to handle message appear from network
 * @author ljnk975
 *
 */
public interface IMessageHander {

	/**
	 * Handle a message from network
	 * @param mss message read from network
	 */
	public void handleMessage(Message mss);

	/**
	 * Handle connected event
	 */
	public void onConnected();

	/**
	 * Handle disconnected event
	 */
	public void onDisconnected();

}
