package topica.linh.trungnt9.networking.bai2.server;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Pattern;

import topica.linh.trungnt9.networking.bai2.network.IMessageHander;
import topica.linh.trungnt9.networking.bai2.network.Message;
import topica.linh.trungnt9.networking.bai2.network.Session;
import topica.linh.trungnt9.networking.bai2.network.Message.Tag;

import static topica.linh.trungnt9.networking.bai2.Config.*;

/**
 * Client handler
 * @author ljnk975
 *
 */
final class Client extends Session implements IMessageHander {

	/**
	 * State of session: init, ready and select
	 * @author ljnk975
	 *
	 */
	enum State {
		INIT, READY, SELECT;
	}

	/**
	 * Current state of this session
	 */
	private State state;

	/**
	 * Id of client
	 */
	private int id;

	/**
	 * Create a client with its socket have been accept
	 * @param sc socket accept in serversocket.accept()
	 * @throws IOException if an I/O error occurs when creating the stream, the socket is closed, the socket is not connected, or the socket input has been shutdown
	 */
	Client(Socket sc, int id) throws IOException {
		super(sc);
		this.id = id;
		this.state = State.INIT;
		this.setHandler(this);
	}

	/**
	 * Create a response message with it's cmd code, phone number received and resultcode,<br/>
	 * resultcode = false then resultvalue is 'NOK'<br/>
	 * resultcode = true then resultvalue is 'OK'<br/>
	 * 
	 * @param cmd          Command code
	 * @param phoneNumber  Phone number received
	 * @param resultCode   ResultCode
	 * @return             Message to send to client
	 */
	private Message getResponseMessage(int cmd, String phoneNumber, boolean resultCode) {
		Message mss = new Message(cmd);
		mss.addTag(new Tag(Tag.TAG_PHONE_NUMBER, phoneNumber));
		mss.addTag(new Tag(Tag.TAG_RESULT_CODE, resultCode ? "OK" : "NOK"));
		return mss;
	}

	@Override
	public final void handleMessage(Message mss) {
		/// Print cmd appear
		System.out.println("Client #"+this.id+" mss="+mss);

		try {
			/// Check phone number
			Tag t = mss.getTag(Tag.TAG_PHONE_NUMBER);

			// if can't find tag or phone number format error then response with error message
			if (t != null && Pattern.matches(PHONE_NUMBER_REGEX, t.getValue())) {
				// get phone number
				String phoneNumber = t.getValue();

				/// process mss code
				switch (mss.getCmd()) {
					// cmd authen
					case Message.CMD_AUTHEN:
						// check if state is init
						if (this.state != State.INIT) {
							// if not return NOK
							super.sendMessage(this.getResponseMessage(Message.CMD_AUTHEN, phoneNumber, false));
							return;
						}
						// get tag key
						if ((t = mss.getTag(Tag.TAG_KEY)) != null) {
							// check authen key
							if(t.getValue().equals(KEY_AUTHEN)) {
								// if tag is tag key and key is topica then return ok result and change state to init
								this.state = State.READY;
								super.sendMessage(this.getResponseMessage(Message.CMD_AUTHEN, phoneNumber, true));
							} else
								// if tag key isn't topica return nok
								super.sendMessage(this.getResponseMessage(Message.CMD_AUTHEN, phoneNumber, false));
							return;
						}
						break;
					// cmd insert and commit
					case Message.CMD_INSERT:
					case Message.CMD_COMMIT:
						// check if state is ready
						if (this.state != State.READY) {
							// if not return NOK
							super.sendMessage(this.getResponseMessage(mss.getCmd(), phoneNumber, false));
							return;
						}
						// if commit then change state to select and return ok
						if (mss.getCmd() == Message.CMD_COMMIT) {
							this.state = State.SELECT;
							super.sendMessage(this.getResponseMessage(Message.CMD_COMMIT, phoneNumber, true));
							return;
						}
						// else it is insert
						// get tag name
						if ((t = mss.getTag(Tag.TAG_NAME)) != null) {
							// insert user to server and return ok result code
							Server.gI().addUser(phoneNumber, t.getValue());
							super.sendMessage(this.getResponseMessage(Message.CMD_INSERT, phoneNumber, true));
							return;
						}
						break;
					// cmd select
					case Message.CMD_SELECT:
						// check if state is select
						if (this.state != State.SELECT) {
							// if not return NOK
							super.sendMessage(this.getResponseMessage(mss.getCmd(), phoneNumber, false));
							return;
						}
						// return ok result code and name slected
						mss = this.getResponseMessage(Message.CMD_AUTHEN, phoneNumber, true);
						mss.addTag(new Tag(Tag.TAG_NAME, Server.gI().getNameByPhoneNumber(phoneNumber)));
						super.sendMessage(mss);
						return;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// not recognize cmd then return NA result code
		mss = new Message(Message.CMD_ERROR);
		mss.addTag(new Tag(Tag.TAG_RESULT_CODE, "NA"));
		super.sendMessage(mss);
	}

	@Override
	public void onConnected() {
	}

	@Override
	public void onDisconnected() {
		System.out.println("Client #"+id+" disconnected!");
	}

}
