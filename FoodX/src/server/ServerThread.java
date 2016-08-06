package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import database.DataFetcher;
import database.Message;
import database.ServerDatabase;
import restaurant.Restaurant;

public class ServerThread extends Thread {
	private ObjectInputStream serverInputStream;
	private ObjectOutputStream serverOutputStream;
	private ServerDatabase sd;
	public ServerThread(Socket s, Server server) {
		try {
			serverOutputStream = new ObjectOutputStream(s.getOutputStream());
			serverInputStream = new ObjectInputStream(s.getInputStream());
			sd = new ServerDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}

	private void fetchData(String zipcode) {
		DataFetcher dt = new DataFetcher();

		ArrayList<Restaurant> res = new ArrayList<Restaurant>();
		System.out.println("Fetching Data");

		res = dt.fetch(zipcode);
		System.out.println("Data Recieved");
		Message obj = new Message();
		obj.setMessageID(3);
		obj.setRestaurant(res);
		try {
			serverOutputStream.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Exiting");

	}

	private void interpretMessage(Message obj) {
		System.out.println("Interpretting Message in Server Thread");
		int id = obj.getMessageID();
		System.out.println(id);
		switch (id) {
		// Restaurant
		case 1: processLogin(obj);
			break;
		case 2: processRegister(obj);
			break;
		case 3: 
			fetchData(obj.getZipcode());
			break;
			
		default:
			System.out.println("Default");
		}
	}

	private void processLogin(Message obj) {
		Message obj1 = new Message();
		obj1.setMessageID(1);
		if (sd.Authenticate_Login(obj.getUser(), obj.getPassword())){
			obj1.setMessage("Authenticated");
		}
		else{
			obj1.setMessage("Not Authenticated");
		}
		try {
			serverOutputStream.writeObject(obj1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	private void processRegister(Message obj) {
		Message obj1 = new Message();
		obj1.setMessageID(2);
		if (sd.Register_User(obj.getUser(), obj.getPassword())){
			obj1.setMessage("Registered");
		}
		else{
			obj1.setMessage("Not Registered");
		}
		try {
			serverOutputStream.writeObject(obj1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			Message obj = null;
			try {
				obj = (Message) serverInputStream.readObject();
				System.out.println("Something recieved");
			} catch (EOFException e) {
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			if (obj != null)
				interpretMessage(obj);
		}
	}

}
