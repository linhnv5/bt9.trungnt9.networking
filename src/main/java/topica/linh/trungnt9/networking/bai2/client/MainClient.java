package topica.linh.trungnt9.networking.bai2.client;

import topica.linh.trungnt9.networking.bai2.network.IMessageHander;
import topica.linh.trungnt9.networking.bai2.network.Message;
import topica.linh.trungnt9.networking.bai2.network.Session;

import static topica.linh.trungnt9.networking.bai2.Config.*;

import java.util.Scanner;

public class MainClient {

	/**
	 * Object to wait response from server
	 */
	public static Object waitingObject = new Object();

	public static void main(String[] args) {
		// create session
		Session session = new Session();
		session.setHandler(new IMessageHander() {
			@Override
			public void handleMessage(Message mss) {
				System.out.println(mss);
				synchronized (waitingObject) {
					waitingObject.notify();
				}
			}
			@Override
			public void onConnected() {
				System.out.println("Connected!");
			}
			@Override
			public void onDisconnected() {
				System.out.println("Disconnected");
				System.exit(0);
			}
		});
		try {
			// Connecting to server
			System.out.println("Connecting to server");
			session.connect("localhost", port);

			// Client input
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
		loop1:
			while(session.isConnected()) {
				// input a command
				System.out.println("Input command: ");
				String line = sc.nextLine();
				// process command
				String[] as = line.split(" ");
				if(as.length % 2 == 0) {
					System.out.println("Cmd error");
					continue;
				}
				// get command
				int command;
				switch(as[0]) {
					case "AUTHEN":
						command = Message.CMD_AUTHEN;
						break;
					case "INSERT":
						command = Message.CMD_INSERT;
						break;
					case "COMMIT":
						command = Message.CMD_COMMIT;
						break;
					case "SELECT":
						command = Message.CMD_SELECT;
						break;
					default:
						System.out.println("CMD Code: "+as[0]+" Error!");
						continue;
				}
				// create send message
				Message sendMss = new Message(command);
				// process tags
				for(int i = 1; i < as.length; i+=2) {
					String tagname = as[i];
					String value = as[i+1];
					int tag;
					switch(tagname) {
						case "Key":
							tag = Message.Tag.TAG_KEY;
							break;
						case "PhoneNumber":
							tag = Message.Tag.TAG_PHONE_NUMBER;
							break;
						case "Name":
							tag = Message.Tag.TAG_NAME;
							break;
						default:
							System.out.println("Tag Code: "+tagname+" Error!");
							continue loop1;
					}
					sendMss.addTag(new Message.Tag(tag, value));
				}
				// send message
				session.sendMessage(sendMss);
				synchronized (waitingObject) {
					waitingObject.wait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
