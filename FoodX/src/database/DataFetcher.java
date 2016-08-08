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
	private class LatLon {
		public String lat;
		public String lon;
		@SuppressWarnings("unused")
		public LatLon(String lat, String lon) {
			this.lat = lat;
			this.lon = lon;
		}
	}
	//	private static final String grub_hub_begin_url = "https://www.grubhub.com/search?orderMethod=delivery&locationMode=DELIVERY"; //add &latitude=XXXXX&longitude=XXXX
	private static final String LA_bite_url = "https://www.labite.com/food-delivery-"; // add zip
	private static final String yelp_begin_url = "http://www.yelp.com/search?find_desc=Restaurants&find_loc=";
	private static final String yelp_end_url = "&start=0&attrs=PlatformDelivery&ytp_st=delivery"; // construct with yelp_begin_url + zip + yelp_end_url


	public static void main(String [] args) {
		DataFetcher df = new DataFetcher();
		ArrayList<Restaurant> rests = df.fetch("90004");
		
		for(Restaurant r : rests) {
			System.out.println(r);
			System.out.println(r.getImageURL());
			System.out.println();
		}
		
	}
	
	private HashMap<String,LatLon> latlon;
	
	public DataFetcher() {

	}
	
	public ArrayList<Restaurant> fetch(String zip) {
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		if(zip.length() < 5) { return new ArrayList<Restaurant>(); }
		System.out.println("before yelp");
		restaurants.addAll(fetchYelp(zip));
		System.out.println("after yelp");
		//restaurants.addAll(fetchGrubHub(zip));
		System.out.println("before LABite");
		restaurants.addAll(fetchLABite(zip));
		System.out.println("after LABite");
		return restaurants;
	}
	
	private ArrayList<Restaurant> fetchLABite(String zip) {
		if(zip == null) { return new ArrayList<Restaurant>(); }
		String request_url = LA_bite_url + zip;
		URL LAbite;
		
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		ArrayList<String> restaurantNames = new ArrayList<String>();
		ArrayList<String> urlToOrders = new ArrayList<String>();
		ArrayList<String> imageURLs = new ArrayList<String>();
		
		try {
			System.out.println("before LA fetch");
			LAbite = new URL(request_url);
			URLConnection uc = LAbite.openConnection();
			BufferedReader in = new BufferedReader(
	                new InputStreamReader(
	                uc.getInputStream()));
			String inputLine;
			System.out.println("after LA fetch");
			
			while ((inputLine = in.readLine()) != null)  {
				String restaurantName;
				String urlToOrder;		
				String imageURL;
				
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
				else if(inputLine.contains("<img itemprop=")) {
					imageURL = inputLine.substring(inputLine.indexOf("src=") + 7, inputLine.indexOf("'/>"));
					imageURLs.add(imageURL);
				}
				if(restaurantNames.size() > 15) {
					break;
				}
			}
			in.close();
			
			
			for(int i=0 ; i < restaurantNames.size(); i++) {
				Restaurant rest = new Restaurant(restaurantNames.get(i),"","",urlToOrders.get(i));
				rest.setImage("http://" + imageURLs.get(i));
				restaurants.add(rest);
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
				}
				
			}
			in.close();
			
			for(int i=0 ; i < restaurantNames.size(); i++) {
				Restaurant rest = new Restaurant(restaurantNames.get(i),"","",urlToOrders.get(i));
				rest.setImage("http://" + images.get(i + 1)); // skip first image, idk why
				restaurants.add(rest);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return restaurants;
	}
	
	@SuppressWarnings("unused")
	private String getLonAndLat(String zip) {
		String lat = "&latitude=";
		String lon = "&longitude=";
		LatLon ll = latlon.get(zip);
		lat = lat + ll.lat;
		lon = lon + ll.lon;
		return lat + lon;
	}
	
	
}
