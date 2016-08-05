package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import database.DataFetcher;
import database.Message;
import restaurant.Restaurant;

public class ServerThread extends Thread {
	private ObjectOutputStream serverOutputStream;
	private ObjectInputStream serverInputStream;
	public ServerThread(Socket s, Server server) {
		try {
			serverOutputStream = new ObjectOutputStream(s.getOutputStream());
			serverInputStream = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}
	
	@Override
	public void run(){
		while (true){
			Message obj = null;
			try {
				obj = (Message) serverInputStream.readObject();
				System.out.println("Something recieved");
			}catch (EOFException e){}
			catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			if (obj!=null)
			interpretMessage(obj);
		}
	}
	
	private void interpretMessage(Message obj) {
		System.out.println("Interpretting Message in Server Thread");
		int id = obj.getMessageID();
		
		switch (id){
		// Restaurant
		case 2: 
			fetchData(obj.getZipcode());
			break;
		default: System.out.println("Default");
		
		}
		
	}

	private void fetchData(String zipcode) {
		DataFetcher dt = new DataFetcher();
		
		ArrayList<Restaurant> res = new ArrayList<Restaurant>();
		System.out.println("Fetching Data");
		
		res = dt.fetch(zipcode);
		System.out.println ("Data Recieved");
		Message obj = new Message();
		obj.setMessageID(1);
		obj.setRestaurant(res);
		try {
			serverOutputStream.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Exiting");
		
		
	}
	
	
	
}
