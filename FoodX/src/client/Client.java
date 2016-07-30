package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import restaurant.Menu;
import restaurant.Restaurant;
import restaurant.RestaurantList;

public class Client {
	private ObjectOutputStream clientOutputStream;
	private ObjectInputStream clientInputStream;
	private Socket socket;
	public Client(String hostname, int port){
		socket = null;
		try {
			socket = new Socket(hostname, port);
			clientInputStream = new ObjectInputStream (socket.getInputStream());
			clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			System.out.println("IOE Exception while initialising Socket: " + e.getMessage());
		}
		
	}
	
	public void sendRestaurantRequest(int zipcode){
		try {
			clientOutputStream.writeObject(zipcode);
			// always flush, lol.
			clientOutputStream.flush();
		} catch (IOException ioe) {
			System.out.println ("IOE Exception Occured in sendRequest: " + ioe.getMessage());
		}
		
	}
	
	public RestaurantList getRestaurantlist(){
		RestaurantList list = null;
		try {
			Object obj = clientInputStream.readObject();
			if (obj instanceof RestaurantList){
				list = (RestaurantList) clientInputStream.readObject();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	public void sendMenuRequest(Restaurant restaurant){
		try {
			clientOutputStream.writeObject(restaurant);
			clientOutputStream.flush();
		} catch (IOException ioe) {
			System.out.println ("IOE Exception Occured in sendRequest: " + ioe.getMessage());
		}
		
	}
	
	public Menu getMenu(){
		Menu list = null;
		try {
			Object obj = clientInputStream.readObject();
			if (obj instanceof Menu){
				list = (Menu) clientInputStream.readObject();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	
	
	public static void main(String args[]){
		
	}
}
