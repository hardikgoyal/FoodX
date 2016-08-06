package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String args[]) {
		new Server(6789);
	}

	private ServerSocket socket;

	public Server(int port) {
		try {
			socket = new ServerSocket(port);
			while (true) {
				Socket s = socket.accept();
				new ServerThread(s, this);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
