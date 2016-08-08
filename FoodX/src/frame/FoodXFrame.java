package frame;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import client.Client;
import restaurant.Restaurant;

//import FoodX.NewUser.GradientPanel;

public class FoodXFrame extends JFrame {
	private static final long serialVersionUID = 9183816558021947333L;
//	public static void main(String[] args) {
//		FoodXFrame fxf = new FoodXFrame(new Client());
//		fxf.setVisible(true);
//	}

	private JTextField zipCodeEnter;
	private GridBagLayout grid;
	private JPanel gridHolder;
	private GridBagConstraints gbc;
	private JScrollPane jsp;
	private Box un;
	private JPanel loading;
	private Client cd;

	public FoodXFrame(Client cd, String userType) {

		this.cd = cd;
		
		// MAIN GUI
		setTitle("Welcome to FoodX");
		setSize(860, 580);
		setLocation(200, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		// Enter zip-code at top of panel
		JLabel zipCodeLabel = new JLabel("Please Enter a Zip Code ... ");
		zipCodeLabel.setOpaque(false);
		zipCodeEnter = new JTextField(10);
		if(userType == "user") zipCodeEnter.setText(cd.getLastEntry());
		//if(userType == "user") zipCodeEnter.setText("12345");
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
		jsp.getVerticalScrollBar().setUnitIncrement(16);
		gridHolder.setLayout(grid);
		setLayout(new BorderLayout());
		add(un, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		
		//loading shit
		loading = new JPanel();
		loading.setLayout(new BorderLayout());
		ImageIcon icon = new ImageIcon("resources/img/ajax-loader.gif");
		icon.setImageObserver(loading);
		loading.add(new JLabel(icon), BorderLayout.CENTER);
		
		/*
		search.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {				
				addLoading();
				getRestaurants();		
			}
		});
		*/
		search.addActionListener(new LoadRestaurants());
		zipCodeEnter.addActionListener(new LoadRestaurants());
		
		addWindowListener(new WindowAdapter(){

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				cd.addLastEntry(zipCodeEnter.getText());
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				cd.addLastEntry(zipCodeEnter.getText());
			}
			
		});
	}
	
	class LoadRestaurants implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			addLoading();
			getRestaurants();
		}
	}
	
	private boolean networkConnection() {
		Socket socket = null;
		boolean reachable = false;
		try {
			socket = new Socket("www.google.com", 80);
			reachable = true;
		}
		catch (IOException e) {	}
	    finally {            
	    if (socket != null) try { socket.close(); } catch(IOException e) {}
		}
		return reachable;
	}
	
	public void getRestaurants() {
		gridHolder.removeAll();

		new SwingWorker<Void, Object>() {
            @Override
            protected Void doInBackground() throws Exception {
            	System.out.println("STARTING TO SEARCH");
        		ArrayList<Restaurant> list = new ArrayList<Restaurant>();
        		System.out.println("zip code received: " + zipCodeEnter.getText());
        		if(!networkConnection()) {
        			JOptionPane.showMessageDialog(null,
	        				"No network connection","No network connection",JOptionPane.ERROR_MESSAGE
	        		);
        			return null;
        		}
        		list = cd.getRestaurantlist(zipCodeEnter.getText());
        		System.out.println("Restaurants Received, Total Restaurants:" + list.size());
        		
        		if(list.isEmpty()) {
	        		JOptionPane.showMessageDialog(null,
	        				"No results found","No results found",JOptionPane.ERROR_MESSAGE
	        		);
        		}
        		else {
        			displayRestaurants(list, gridHolder);
        		}
        		
        		validate();
        		repaint();
        		System.out.println("DONE WITH DISPLAYING");
				return null;
            }

            @Override
            protected void done() {
            	FoodXFrame.this.remove(loading);
            	FoodXFrame.this.repaint();
            }
        }.execute();
		
	}
	
	public void addLoading() {
		add(loading);
		validate();
		repaint();
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
				System.out.println("image url: " + curr.getImageURL());
				url = new URL(curr.getImageURL());
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
