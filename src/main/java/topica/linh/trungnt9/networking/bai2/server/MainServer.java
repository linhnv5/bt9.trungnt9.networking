package topica.linh.trungnt9.networking.bai2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static topica.linh.trungnt9.networking.bai2.Config.*;

public class MainServer {

	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			// Tao ket noi
			System.out.println("Creating Server");
			server = new ServerSocket(port);

			// Print created server
			System.out.println("Server Listening on port "+port);

			// accept client
			int id = 0;
			Socket sc;
			while((sc = server.accept()) != null) {
				System.out.println("New client connected id=#"+id);
				new Client(sc, id++);
			}
		} catch (IOException e) {
			System.out.println("Exception: "+e);
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch(Exception e) {
			}
			System.out.println("Server Closed");
		}
	}

}
