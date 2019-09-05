package topica.linh.trungnt9.networking.bai2.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * Define a server level session have 4 state init, ready, select
 * @author ljnk975
 *
 */
public class Session implements ISession {

	/**
	 * Network socket interface
	 */
	private Socket sc;

	/**
	 * Input stream to read message
	 */
	private DataInputStream  dis;

	/**
	 * Output stream to write message
	 */
	private DataOutputStream dos;

	/**
	 * Flag set if socket is connecting to server
	 */
	private boolean connecting;

	/**
	 * Flag set if socket still alive
	 */
	private boolean connected;

	/**
	 * Handler to process message
	 */
	private IMessageHander handler;

	/**
	 * Message collector that collect message from socket and handle it
	 * @author ljnk975
	 *
	 */
	private class MessageCollector implements Runnable {
		@Override
		public void run() {
			try {
				while(connected) {
					int lentOfMessage = dis.readInt(); // read lent of message

					byte[] data = new byte[lentOfMessage]; // buffer to read message
					dis.readFully(data, 0, data.length); // read message byte to buffer

					Message mss = Message.readMessage(data); // read message
					if(mss != null)
						handler.handleMessage(mss); // if no error, handle that message
				}
			} catch (Exception e) {
				// if exception when connected then 
				if(connected) {
					handler.onDisconnected();
					close();
				}
			}
		}
	}

	/**
	 * A list contain sending message
	 */
	private Vector<Message> listOfSendingMessage = new Vector<Message>();

	/**
	 * Lock the sender if no message to send
	 */
	private Object sendLockingObject = new Object();

	/**
	 * Sender message use to send message through network
	 * @author ljnk975
	 *
	 */
	private class MessageSender implements Runnable {
		@Override
		public void run() {
			try {
				while(connected) {
					// Waiting 
					synchronized (sendLockingObject) {
						try {
							sendLockingObject.wait();
						} catch (InterruptedException e) {
						}
					}
					while(listOfSendingMessage.size() > 0) {
						// get data
						byte[] data = listOfSendingMessage.remove(0).toByteArray();

						// send message
						dos.writeInt(data.length); // send length
						dos.write(data, 0, data.length); // send data
					}
				}
			} catch(Exception e) {
			}
		}
	}

	/**
	 * Thread handle of collector
	 */
	private Thread collectorThread;

	/**
	 * Thread handle of sender
	 */
	private Thread senderThread;

	/**
	 * Default constructor, using setSocket to begin network
	 */
	public Session() {
	}

	/**
	 * Create server session by using socket accepted
	 * @param sc the java socket interface
	 * @throws IOException  if an I/O error occurs when creating the stream, the socket is closed, the socket is not connected, or the socket input has been shutdown
	 */
	public Session(Socket sc) throws IOException {
		this.setSocket(sc);
	}

	/**
	 * Set socket to start network, if this session is connected then close connect
	 * @param sc Socket accept by server or have been connect to another server
	 * @throws IOException if an I/O error occurs when creating the stream, the socket is closed, the socket is not connected, or the socket input has been shutdown
	 */
	public final void setSocket(Socket sc) throws IOException {
		// if in connect then close prev connection
		if(this.connected)
			this.close();

		// set connect
		this.sc = sc;
		this.dis = new DataInputStream(sc.getInputStream());
		this.dos = new DataOutputStream(sc.getOutputStream());

		// set connected flag
		this.connected = true;

		// start thread
		(this.collectorThread = new Thread(new MessageCollector())).start();
		(this.senderThread = new Thread(new MessageSender())).start();
	}

	/**
	 * @return the connected
	 */
	public final boolean isConnected() {
		return connected;
	}

	/**
	 * Blocking call, use to connect to server, if that session is connected or connecting then just return
	 * @param host hostname
	 * @param port port
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public final void connect(String host, int port) throws UnknownHostException, IOException {
		if(this.connecting || this.connected)
			return;

		this.connecting = true;
		try {
			this.setSocket(new Socket(host, port));
		} finally {
			this.connecting = false;
		}
	}

	@Override
	public final void setHandler(IMessageHander handler) {
		this.handler = handler;
	}

	@Override
	public void sendMessage(Message mss) {
		// add message to queue and notify sender thread
		this.listOfSendingMessage.add(mss);
		synchronized (this.sendLockingObject) {
			this.sendLockingObject.notify();
		}
	}

	@Override
	public void close() {
		// set connected false to stop collector and sender
		this.connecting = false;
		this.connected = false;

		// clear sending message
		this.listOfSendingMessage.clear();

		// interrupt collector and sending thread
		if(this.collectorThread != null)
			this.collectorThread.interrupt();
		
		if(this.senderThread != null)
			this.senderThread.interrupt();

		// Close socket
		try {
			this.dis.close();
		} catch (IOException e) {
		}
		try {
			this.dos.close();
		} catch (IOException e) {
		}
		try {
			this.sc.close();
		} catch (IOException e) {
		}
	}

}
