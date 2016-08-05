package restaurant;

import java.io.Serializable;

public class Restaurant implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String name;
	public String address;
	public String phone;
	public String orderURL;
	
	public Restaurant(String name, String address, String phone, String orderURL){
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.orderURL = orderURL;
	}
	
	@Override
	public String toString() {
		return name + '\n' + phone + '\n' + address + '\n' + orderURL;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getOrderURL(){
		return this.orderURL;
	}
}
