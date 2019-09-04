package topica.linh.trungnt9.networking.bai2.network;

/**
 * Define a session interface to process message
 * @author ljnk975
 *
 */
public interface ISession {

	/**
	 * Set handler message
	 * @param handler handler to process message
	 */
	public void setHandler(IMessageHander handler);

	/**
	 * Send a message
	 * @param mss message to send
	 */
	public void sendMessage(Message mss);

	/**
	 * Close network
	 */
	public void close();

}
