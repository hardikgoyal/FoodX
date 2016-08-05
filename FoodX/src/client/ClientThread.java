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
	private Lock mLock;
	private Condition ListRecieved;
	private ObjectOutputStream clientOutputStream;
	private ObjectInputStream clientInputStream;
	private ArrayList<Restaurant> reslist;
	public ClientThread(Socket socket, Client client) {
		mLock = new ReentrantLock();
		ListRecieved = mLock.newCondition();
		reslist = null;
		try {
			clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
			clientInputStream = new ObjectInputStream (socket.getInputStream());
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
				obj = (Message) clientInputStream.readObject();
				System.out.println("Recieved Something: " + obj==null );
			}catch (EOFException e){} 
			catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			if (obj!=null)
			interpretMessage(obj);
			
		}
	}

	private void interpretMessage(Message obj) {
		try{
			mLock.lock();
			int id = obj.getMessageID();
			System.out.println("Message Recieved");
			switch (id){
			
			// Restaurant
			case 1: reslist = obj.getRestaurant();
			ListRecieved.signalAll();
			System.out.println("reslist initialised");
				break;
			default: System.out.println("Default");
			}
		}
		finally{
			mLock.unlock();
		}
		
		
	}
	

	public ArrayList<Restaurant> getRestaurant(String zipcode) {
		try{
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
		} finally{
			mLock.unlock();
			
		}
		return reslist;
		
		
	}
	
}
