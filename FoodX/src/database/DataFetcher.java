package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import restaurant.Restaurant;

public class DataFetcher {
	private static final String yelp_begin_url = "http://www.yelp.com/search?find_desc=Restaurants&find_loc=";
	private static final String yelp_end_url = "&start=0&ytp_st=delivery"; // construct with yelp_begin_url + zip + yelp_end_url
	private static final String grub_hub_begin_url = "https://www.grubhub.com/search?orderMethod=delivery&locationMode=DELIVERY"; //add &latitude=XXXXX&longitude=XXXX
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
		ArrayList<Restaurant> rests = df.fetch("90007");
		for(Restaurant r : rests) {
			//System.out.println(r);
			//System.out.println();
		}
	}
	
	public DataFetcher() {
		latlon = new HashMap<String,LatLon>();
		try {
			Scanner sc = new Scanner(new BufferedReader(new FileReader(new File("resources/zipcode.csv"))));
			sc.nextLine();
			while(sc.hasNext()) {
				String[] line = sc.nextLine().split(",");
				if(line.length > 5) {
					latlon.put(line[0].substring(1, line[0].length()-1), new LatLon(line[3].substring(1, line[3].length()-1),line[4].substring(1, line[4].length()-1)));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Restaurant> fetch(String zip) {
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		restaurants.addAll(fetchYelp(zip));
		//restaurants.addAll(fetchGrubHub(zip));
		return restaurants;
	}
	
	private ArrayList<Restaurant> fetchGrubHub (String zip) {
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		String lonAndLat = getLonAndLat(zip);
		System.out.println(lonAndLat);
		String request_url = grub_hub_begin_url + lonAndLat + "facetSet=umami&pageSize=20&hideHateos&variationId=default-impressionScoreBaseBuffed-20160317&countOmittingTimes";
		URL grubHub;
		
		try {

			grubHub = new URL(request_url);
			HttpURLConnection uc = (HttpURLConnection) grubHub.openConnection();
				
			
			BufferedReader in = new BufferedReader(
	                new InputStreamReader(
	                uc.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)  {
				System.out.println(inputLine);
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
	
	private ArrayList<Restaurant> fetchYelp(String zip) {
		
		String request_url = yelp_begin_url + zip + yelp_end_url;
		URL yelp;
		
		
		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		ArrayList<String> restaurantNames = new ArrayList<String>();
		ArrayList<String> urlToOrders = new ArrayList<String>();
		ArrayList<String> addreses = new ArrayList<String>();
		ArrayList<String> phones = new ArrayList<String>();
		
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
				
			}
			in.close();
			
			for(int i=0 ; i < restaurantNames.size(); i++) {
				restaurants.add(new Restaurant(restaurantNames.get(i),addreses.get(i),phones.get(i),urlToOrders.get(i)));
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return restaurants;
	}
	
}
