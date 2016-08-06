package database;

import java.io.Serializable;
import java.util.ArrayList;

import restaurant.Restaurant;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2334490287694436749L;
	private boolean authenticated, registered;
	private int messageID;
	private ArrayList<Restaurant> restaurant;
	private String user, password, zipcode;
	private String message;
	public Message() {

	}

	public int getMessageID() {
		return messageID;
	}

	public String getPassword() {
		return password;
	}

	public ArrayList<Restaurant> getRestaurant() {
		return restaurant;
	}

	public String getUser() {
		return user;
	}

	public String getZipcode() {
		return zipcode;
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

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRestaurant(ArrayList<Restaurant> restaurant) {
		this.restaurant = restaurant;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
