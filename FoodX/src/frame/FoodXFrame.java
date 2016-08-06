package frame;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	private GridBagLayout grid;
	private JPanel gridHolder;
	private GridBagConstraints gbc;
	private JScrollPane jsp;
	private Box un;

	public FoodXFrame() {

		// MAIN GUI
		setTitle("Welcome to FoodX");
		setSize(860, 580);
		setLocation(200, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Enter zip-code at top of panel
		JLabel zipCodeLabel = new JLabel("Please Enter a Zip Code ... ");
		zipCodeLabel.setOpaque(false);
		zipCodeEnter = new JTextField(10);
		JButton search = new JButton("Search");

		un = Box.createHorizontalBox();
		un.setOpaque(false);
		un.add(zipCodeLabel);
		un.add(zipCodeEnter);
		un.add(search);
		
		
		gridHolder = new JPanel();
		grid = new GridBagLayout();
		gbc = new GridBagConstraints();
		jsp = new JScrollPane(gridHolder);
		gridHolder.setLayout(grid);
		setLayout(new BorderLayout());
		add(un, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		
		search.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("STARTING TO SEARCH");
				Client cd = new Client();
				ArrayList<Restaurant> list = new ArrayList<Restaurant>();
				System.out.println("zip code received: " + zipCodeEnter.getText());
				list = cd.getRestaurantlist(zipCodeEnter.getText());
				System.out.println("Restaurants Received, Total Restaurants:" + list.size());

				//setUpDisplay();
				displayRestaurants(list, gridHolder);
				validate();
				repaint();
				System.out.println("DONE WITH DISPLAYING");
			}
		});
		
	}
	
	public void setUpDisplay(){
		gridHolder = new JPanel();
		grid = new GridBagLayout();
		gbc = new GridBagConstraints();
		jsp = new JScrollPane(gridHolder);
		
		gridHolder.setLayout(grid);
		setLayout(new BorderLayout());
		
		add(jsp, BorderLayout.CENTER);
	}

	public void displayRestaurants(ArrayList<Restaurant> list, JPanel gridDisplay) {

		URL url = null;
		Image image = null;
		ImageIcon ic = null;
		Image image1 = null;
		Image newImage = null;
		
		int x = 0;
		int y = 0;
		
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
			newImage = image1.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
			ic = new ImageIcon(newImage);

			JLabel label = new JLabel("", ic, SwingConstants.CENTER);

			label.addMouseListener(new SendOrder(curr));
			
			JPanel restaurantBox = new JPanel();
			restaurantBox.setLayout(new BorderLayout());
			restaurantBox.add(label, BorderLayout.CENTER);
			
			JLabel name = new JLabel(curr.getName());
			System.out.println(curr.getName());
			restaurantBox.add(name, BorderLayout.SOUTH);
			restaurantBox.setVisible(true);
			gbc.gridx = x;
			gbc.gridy = y;
			gbc.insets = new Insets(3,3,3,3);
			gridDisplay.add(restaurantBox, gbc);
			if(x != 3) x++;
			else{
				x = 0;
				y++;
			}
		}
	}
	
	class SendOrder implements MouseListener{
		private Restaurant curr;
		public SendOrder(Restaurant curr1){
			curr = curr1;
		}
		
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
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}
}
