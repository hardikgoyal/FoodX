package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import restaurant.Restaurant;

public class Client {
//	private ObjectOutputStream clientOutputStream;
//	private ObjectInputStream clientInputStream;
	private Socket socket;
	private ClientThread ct;
	public Client(){
		socket = null;
		try {
			socket = new Socket("localhost", 6789);
			ct = new ClientThread(socket, this);
		} catch (IOException e) {
			System.out.println("IOE Exception while initialising Socket: " + e.getMessage());
		}
		
	}


	
	public ArrayList<Restaurant> getRestaurantlist(String zipcode){
		System.out.println("Restaurant Request Recieved");
		return ct.getRestaurant(zipcode);
		
	}
	
//	public void sendMenuRequest(Restaurant restaurant){
//		try {
//			clientOutputStream.writeObject(restaurant);
//			clientOutputStream.flush();
//		} catch (IOException ioe) {
//			System.out.println ("IOE Exception Occured in sendRequest: " + ioe.getMessage());
//		}
//		
//	}
//	

	
	
	public static void main(String args[]){
		
	}
}
