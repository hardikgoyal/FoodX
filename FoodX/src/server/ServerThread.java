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
	private Socket s;
	public ServerThread(Socket s, Server server) {
		try {
			this.s = s;
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
		res = dt.fetch(zipcode);
		Message obj = new Message();
		obj.setMessageID(3);
		obj.setRestaurant(res);
		try {
			serverOutputStream.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		case 100:
			addZip(obj.getUser(),obj.getMessage());
			break;
		case 101:
			getZip(obj.getUser());
		default:
			System.out.println("Default");
		}
	}

	private void addZip(String user, String message) {
		sd.addLastZip(user, message);
		
	}
	
	private void getZip(String user) {
		String str = sd.getLastZip(user);
		Message obj = new Message ();
		obj.setMessageID(101);
		obj.setMessage(str);
		try {
			serverOutputStream.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processLogin(Message obj) {
		Message obj1 = new Message();
		obj1.setMessageID(1);
		obj1.setMessage(sd.Authenticate_Login(obj.getUser(), obj.getPassword()));
		try {
			serverOutputStream.writeObject(obj1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	private void processRegister(Message obj) {
		Message obj1 = new Message();
		obj1.setMessageID(2);
		obj1.setMessage (sd.Register_User(obj.getUser(), obj.getPassword()));
		try {
			serverOutputStream.writeObject(obj1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!s.isClosed()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Message obj = null;
			try {
				obj = (Message) serverInputStream.readObject();
				System.out.println("Something recieved");
			} catch (EOFException e) {
				System.out.println("infinite loop happens here");
				try {
					s.close();
				} catch (IOException ioe) {
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			if (obj != null)
				interpretMessage(obj);
		}
		
	}

}
