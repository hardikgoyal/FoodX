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
	public ClientThread(Socket socket, Client client) {
		mLock = new ReentrantLock();
		message = "";
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
		start();
	}

	public ArrayList<Restaurant> getRestaurant(String zipcode) {
		try {
			mLock.lock();
			System.out.println("In Client Thread Restaurnat");
			Message obj = new Message();
			obj.setMessageID(2);
			obj.setZipcode(zipcode);
			try {
				clientOutputStream.writeObject(obj);
				System.out.println("Request Sent");
				ListRecieved.await();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Request returned");
		} finally {
			mLock.unlock();

		}
		return reslist;

	}

	private void interpretMessage(Message obj) {
		try {
			mLock.lock();
			int id = obj.getMessageID();
			System.out.println("Message Recieved");
			switch (id) {

			// Restaurant
			case 1:
				reslist = obj.getRestaurant();
				ListRecieved.signalAll();
				System.out.println("reslist initialised");
				break;
			case 2: 
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
		try{
			Message obj = new Message ();
			obj.setMessageID(2);
			obj.setUser(user);
			obj.setPassword(password);
			try {
				clientOutputStream.writeObject(obj);
				System.out.println("Request Sent");
				ListRecieved.await();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Request returned");
		} finally {
			mLock.unlock();
		}
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
				ListRecieved.await();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Request returned");
		} finally {
			mLock.unlock();
		}
		return str;
	}

}
