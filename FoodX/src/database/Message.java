package database;

import java.io.Serializable;
import java.util.ArrayList;

import restaurant.Restaurant;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2334490287694436749L;
	private int messageID;
	private ArrayList<Restaurant> restaurant;
	private String user, password, zipcode;
	private boolean authenticated, registered;
	public Message(){
		
	}
	public int getMessageID() {
		return messageID;
	}
	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}
	public ArrayList<Restaurant> getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(ArrayList<Restaurant> restaurant) {
		this.restaurant = restaurant;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}
	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return registered;
	}
	
}
