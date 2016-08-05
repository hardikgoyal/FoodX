package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket socket;
	private ObjectOutputStream serverOutputStream;
	private ObjectInputStream serverInputStream;
	public Server(int port){
		try {
			socket = new ServerSocket(port);
			while(true){
				Socket s = socket.accept();
				new ServerThread(s, this);
				serverInputStream = new ObjectInputStream(s.getInputStream());
				serverOutputStream = new ObjectOutputStream(s.getOutputStream());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
