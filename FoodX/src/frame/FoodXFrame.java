package frame;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import client.Client;
import restaurant.Restaurant;

//import FoodX.NewUser.GradientPanel;

public class FoodXFrame extends JFrame {
	private static final long serialVersionUID = 9183816558021947333L;
	public static void main(String[] args) {
		FoodXFrame fxf = new FoodXFrame();
		fxf.setVisible(true);
	}

	private JTextField zipCodeEnter;
	private GridLayout grid;
	private JPanel gridHolder;

	public FoodXFrame() {

		// MAIN GUI
		setTitle("Welcome to Food X");
		setSize(640, 480);
		setLocation(200, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Enter zip-code at top of panel
		JLabel zipCodeLabel = new JLabel("Please Enter a Zip Code ... ");
		zipCodeLabel.setOpaque(false);
		zipCodeEnter = new JTextField(10);

		Box un = Box.createHorizontalBox();
		un.setOpaque(false);
		un.add(zipCodeLabel);
		un.add(zipCodeEnter);
		
		Client cd = new Client();
		ArrayList<Restaurant> list = new ArrayList<Restaurant>();
		list = cd.getRestaurantlist("90007");
		System.out.println("Restaurants Recieved, Total Restaurants:" + list.size());

		int rows = list.size() / 4;
		//System.out.println("rows: " + rows);
		gridHolder = new JPanel();
		grid = new GridLayout(rows + 1, 4, 2, 2);
		gridHolder.setLayout(grid);
		setLayout(new BorderLayout());
		add(un, BorderLayout.NORTH);
		add(gridHolder, BorderLayout.CENTER);

		displayRestaurants(list, gridHolder);
	}

	public void displayRestaurants(ArrayList<Restaurant> list, JPanel gridDisplay) {
		
//		Restaurant r = new Restaurant("Wokcano", "123 Horsed Ave.", "123-456-7890",
//				"https://www.grubhub.com/restaurant/wokcano-800-w-7th-st-los-angeles/78645");
//		Restaurant r1 = new Restaurant("Wokcano", "123 Horsed Ave.", "123-456-7890",
//				"https://www.grubhub.com/restaurant/pizza-moon-2619-s-western-ave-los-angeles/260810");
		// Restaurant r2 = new Restaurant("Wokcano", "123 Horsed Ave.",
		// "123-456-7890",
		// "https://www.grubhub.com/restaurant/wokcano-800-w-7th-st-los-angeles/78645");
		// list.add(r);
		// list.add(r1);
		// list.add(r2);

		URL url = null;
		Image image = null;
		ImageIcon ic = null;
		Image image1 = null;
		Image newImage = null;

//		Client cd = new Client();
//		list = cd.getRestaurantlist("90007");
//		System.out.println("Restaurants Recieved, Total Restaurants:" + list.size());
		for (int i = 0; i < list.size(); i++) {
			
			Restaurant curr = list.get(i);

			try {
				url = new URL("http://www.lasplash.com/uploads//3/wokcano_restaurants_logo.jpg");
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			try {
				image = ImageIO.read(url);
				ic = new ImageIcon(image);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			image1 = ic.getImage();
			newImage = image1.getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH);
			ic = new ImageIcon(newImage);

			JLabel label = new JLabel("", ic, SwingConstants.CENTER);

			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					System.out.println("Click at: " + e.getPoint());
					Desktop d = Desktop.getDesktop();
					try {
						d.browse(new URI(curr.getOrderURL()));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			JPanel restaurantBox = new JPanel();
			restaurantBox.setLayout(new BorderLayout());
			restaurantBox.add(label, BorderLayout.CENTER);
			
			JLabel name = new JLabel(curr.getName());
			System.out.println(curr.getName());
			restaurantBox.add(name, BorderLayout.SOUTH);
			restaurantBox.setVisible(true);
			gridDisplay.add(restaurantBox);
		}
	}
}
