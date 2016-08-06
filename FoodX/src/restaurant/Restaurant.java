 package restaurant;

import java.io.Serializable;

public class Restaurant implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String address;
	public String name;
	public String orderURL;
	public String phone;
	public String imageURL;
	
	public Restaurant(String name, String address, String phone, String orderURL) {
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.orderURL = orderURL;
		this.imageURL = "https://lh3.googleusercontent.com/-_nUVxTlr8uY/Um9fFMZ3gSI/AAAAAAAABmg/9iQ2JRM2QRg/s0/knife+fork+logo.png";
	}
	
	public void setImage(String url) {
		this.imageURL = url;
	}
	
	public String getImageURL() {
		return imageURL;
	}

	public String getName() {
		return this.name;
	}

	public String getOrderURL() {
		return this.orderURL;
	}

	@Override
	public String toString() {
		return name + '\n' + phone + '\n' + address + '\n' + orderURL;
	}
}
