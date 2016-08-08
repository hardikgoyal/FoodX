package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import database.Message;
import frame.AuthorizationPanel;
import restaurant.Restaurant;

public class ClientThread extends Thread {
	private ObjectInputStream clientInputStream;
	private ObjectOutputStream clientOutputStream;
	private Condition ListRecieved;
	private Condition userLogin;
	private Condition userRegistration;
	private Lock mLock;
	private ArrayList<Restaurant> reslist;
	private String message;
	private AuthorizationPanel ap;
	private String user;
	private Condition ziplock;
	private String zip ="";
	public ClientThread(Socket socket, Client client) {
		mLock = new ReentrantLock();
		message = "";
		user = "";
		ziplock = mLock.newCondition();
		ListRecieved = mLock.newCondition();
		userLogin = mLock.newCondition();
		userRegistration = mLock.newCondition();
		
		reslist = null;
		try {
			clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
			clientInputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ap = new AuthorizationPanel(client);
		ap.setVisible(true);
		start();
		
	}

	public ArrayList<Restaurant> getRestaurant(String zipcode) {
		long time = System.currentTimeMillis();
		try {
			mLock.lock();
			Message obj = new Message();
			obj.setMessageID(3);
			obj.setZipcode(zipcode);
			try {
				clientOutputStream.writeObject(obj);
				ListRecieved.await();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			mLock.unlock();
		}
		long finaltime = System.currentTimeMillis() - time;
		System.out.println("Time: " + finaltime);
		return reslist;

	}

	private void interpretMessage(Message obj) {
		try {
			mLock.lock();
			int id = obj.getMessageID();
			System.out.println("Message Recieved: " + id);
			switch (id) {

			// Restaurant
			case 1:message = obj.getMessage();
				userLogin.signalAll();
				break;
				
			case 2: message = obj.getMessage();
				userRegistration.signalAll();
				break;
				
			case 3: reslist = obj.getRestaurant();
				ListRecieved.signalAll();
				System.out.println("reslist initialised");
				break;
			case 101: 
				zip = obj.getMessage();
				ziplock.signalAll();
				break;
				
			default:
				System.out.println("Default");
			}
		} finally {
			mLock.unlock();
		}

	}

	@Override
	public void run() {
		while (true) {
			Message obj = null;
			try {
				obj = (Message) clientInputStream.readObject();
				System.out.println("Recieved Something: " + obj == null);
			} catch (EOFException e) {
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			if (obj != null)
				interpretMessage(obj);
		}
	}

	public String register(String user, String password) {
		String str = "";
		mLock.lock();
		System.out.println("In the registered User");
		try{
			Message obj = new Message ();
			obj.setMessageID(2);
			obj.setUser(user);
			obj.setPassword(password);
			try {
				clientOutputStream.writeObject(obj);
				System.out.println("Register: Request Sent");
				
				userRegistration.await();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Register: Request returned");
		} finally {
			mLock.unlock();
		}
		str = message;
		if (message.startsWith("Registered")){
			this.user = user;
		}
		System.out.println("Register:"  + str);
		message = "";
		return str;
	}

	public String authenticate(String user, String password) {
		String str = "";
		mLock.lock();
		try{
			Message obj = new Message ();
			obj.setMessageID(1);
			obj.setUser(user);
			obj.setPassword(password);
			try {
				clientOutputStream.writeObject(obj);
				System.out.println("Request Sent");
				userLogin.await();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Request returned");
		} finally {
			mLock.unlock();
		}
		str = message;
		if (message.startsWith("Authenticated")){
			this.user = user;
		}
		message ="";
		return str;
	}

	public void addLastEntry(String zipcode) {
		Message obj = new Message();
		obj.setMessageID(100);
		obj.setMessage(zipcode);
		obj.setUser(user);
		try {
			clientOutputStream.writeObject(obj);
			System.out.println("Request Sent");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getLastEntry(){
		mLock.lock();
		
		try {
			Message obj = new Message();
			obj.setMessageID(101);
			obj.setUser(user);
			clientOutputStream.writeObject(obj);
			System.out.println("Request Sent");
			ziplock.await();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			mLock.unlock();
		}
		
		
		return zip;
	}

}
