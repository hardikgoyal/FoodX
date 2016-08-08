package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import restaurant.Restaurant;

public class Client {
	public static void main(String args[]) {
		new Client();
	}
	private ClientThread ct;

	// private ObjectOutputStream clientOutputStream;
	// private ObjectInputStream clientInputStream;
	private Socket socket;

	public Client() {
		socket = null;
		try {
			socket = new Socket("localhost", 6789);
			ct = new ClientThread(socket, this);
		} catch (IOException e) {
			System.out.println("IOE Exception while initialising Socket: " + e.getMessage());
		}

	}


	public String authenticate_user(String user, String password){
		String res = ct.authenticate(user, password);
		return res;
	}
	
	public String register_user(String user, String password){
		String res = ct.register(user, password);
		return res;
	}
	public ArrayList<Restaurant> getRestaurantlist(String zipcode) {
		System.out.println("Restaurant Request Recieved");
		return ct.getRestaurant(zipcode);

	}
<<<<<<< HEAD
	
	public void addLastEntry(String zipcode){
		ct.addLastEntry(zipcode);
	}
=======

>>>>>>> 40ebc06405efd9c92d58956c67eaf0b7d238aa7e
}
