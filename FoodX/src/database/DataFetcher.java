package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import restaurant.Restaurant;


public class DataFetcher {
	private static final String yelp_begin_url = "http://www.yelp.com/search?find_desc=Restaurants&find_loc=";
	private static final String yelp_end_url = "&start=0&ytp_st=delivery"; // construct with yelp_begin_url + zip + yelp_end_url
//	private static final String grub_hub_begin_url = "https://www.grubhub.com/search?orderMethod=delivery&locationMode=DELIVERY"; //add &latitude=XXXXX&longitude=XXXX
	private static final String LA_bite_url = "https://www.labite.com/food-delivery-"; // add zip
	private HashMap<String,LatLon> latlon;


	private class LatLon {
		public String lat;
		public String lon;
		public LatLon(String lat, String lon) {
			this.lat = lat;
			this.lon = lon;
		}
	}
	
	public static void main(String [] args) {
		DataFetcher df = new DataFetcher();
		ArrayList<Restaurant> rests = df.fetch("90004");
		for(Restaurant r : rests) {
			System.out.println(r);
			System.out.println();
		}
		System.out.println(rests.size());
		System.out.println("asdf");
	}
	
	public DataFetcher() {

	}
	
	public ArrayList<Restaurant> fetch(String zip) {
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		restaurants.addAll(fetchYelp(zip));
		//restaurants.addAll(fetchGrubHub(zip));
		restaurants.addAll(fetchLABite(zip));
		return restaurants;
	}
	
	private ArrayList<Restaurant> fetchLABite(String zip) {
		
		String request_url = LA_bite_url + zip;
		URL LAbite;
		
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		ArrayList<String> restaurantNames = new ArrayList<String>();
		ArrayList<String> urlToOrders = new ArrayList<String>();
		
		try {
			LAbite = new URL(request_url);
			URLConnection uc = LAbite.openConnection();
			BufferedReader in = new BufferedReader(
	                new InputStreamReader(
	                uc.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)  {
				
				String restaurantName;
				String urlToOrder;		
				
				if(inputLine.contains("itemprop=\"url menu")) {
					urlToOrder = "https://www.labite.com/" + inputLine.substring(inputLine.indexOf("href='") + "href=\"".length(), inputLine.indexOf("'>"));
					urlToOrders.add(urlToOrder);
				}
				
				
				else if(inputLine.contains("itemprop=\"name\"")) {
					restaurantName = inputLine.substring(inputLine.indexOf("name\">") + "name\">".length(), inputLine.indexOf("</h4>"));
					restaurantName = restaurantName.replace("&#39;", "'");
					restaurantName = restaurantName.replaceAll("&amp;", "&");
					restaurantNames.add(restaurantName);
				}	
			}
			in.close();
			
			
			for(int i=0 ; i < restaurantNames.size(); i++) {
				restaurants.add(new Restaurant(restaurantNames.get(i),"","",urlToOrders.get(i)));
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return restaurants;
	}
	

	private ArrayList<Restaurant> fetchYelp(String zip) {
		
		String request_url = yelp_begin_url + zip + yelp_end_url;
		URL yelp;
		
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		ArrayList<String> restaurantNames = new ArrayList<String>();
		ArrayList<String> urlToOrders = new ArrayList<String>();
		ArrayList<String> addreses = new ArrayList<String>();
		ArrayList<String> phones = new ArrayList<String>();
		ArrayList<String> images = new ArrayList<String>();
		
		try {
			yelp = new URL(request_url);
			URLConnection uc = yelp.openConnection();
			BufferedReader in = new BufferedReader(
	                new InputStreamReader(
	                uc.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)  {
				
				String restaurantName;
				String urlToOrder;
				String address;
				String phone;
				String imageURL;
				//System.out.println(inputLine);
				
				if(inputLine.contains("<span class=\"indexed-biz-name\">")) { 
					restaurantName = inputLine.substring(inputLine.indexOf("><span >") + "><span >".length(), inputLine.indexOf("</span></a>"));
			
					urlToOrder = "http://www.yelp.com/" + inputLine.substring(inputLine.indexOf("href=\"") + "href=\"".length(),inputLine.indexOf("\" data-hovercard-id"));
					restaurantNames.add(restaurantName);
					urlToOrders.add(urlToOrder);
				}
				
				else if(inputLine.contains("<address>")) {
					address = in.readLine();
					address = address.replace("<br>", "\n");
					address = address.substring(12);
					addreses.add(address);
				}
				else if(inputLine.contains("biz-phone")) {
					phone = in.readLine().substring(8);
					phones.add(phone);
				}
				else if(inputLine.contains("photo-box pb-90s")) {
					in.readLine();
					imageURL = in.readLine();
					imageURL = imageURL.substring(imageURL.indexOf("src=") + 7,imageURL.indexOf("\" width=\"90") );
					images.add(imageURL);
					System.out.println(imageURL);
				}
				
			}
			in.close();
			
			for(int i=0 ; i < restaurantNames.size(); i++) {
				restaurants.add(new Restaurant(restaurantNames.get(i),addreses.get(i),phones.get(i),urlToOrders.get(i)));
				if(images.get(i)!= null) {
					restaurants.get(i).setImage(images.get(i));
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return restaurants;
	}
	
	private String getLonAndLat(String zip) {
		String lat = "&latitude=";
		String lon = "&longitude=";
		LatLon ll = latlon.get(zip);
		lat = lat + ll.lat;
		lon = lon + ll.lon;
		return lat + lon;
	}
	
	
}
