package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import client.Client;

public class AuthorizationPanel extends JFrame {

	private static final long serialVersionUID = 9183816558021947333L;
	private NewUser nu;


	private static MessageDigest md;
	private Client cl;

	private FoodXFrame mainframe;

	public AuthorizationPanel(Client client) {

		//instantiate the Main Authorization Panel
		super("Food X Authorization Panel");
		setSize(640,480);
		setLocation(200,200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		cl = client;
		//mainframe = new FoodXFrame(cl);
		nu = new NewUser(this, cl);
		//Interactive Options
		JLabel username = new JLabel("User Name");
		JLabel password = new JLabel("Password  ");
		JTextField usernameBox = new JTextField("", 20);
		usernameBox.setForeground(Color.BLUE);

		JTextField passwordBox = new JPasswordField(20);
		passwordBox.setForeground(Color.BLUE);

		ImageIcon image = new ImageIcon("resources/img/FoodX_Text_Login.png");
		JLabel label = new JLabel(image);
		JPanel picture = new JPanel(new BorderLayout());
		picture.setBackground(Color.CYAN);
		picture.add(label, BorderLayout.CENTER);
		add(picture, BorderLayout.NORTH);

		JLabel label2 = new JLabel("Hello");
		label2.setBackground(Color.BLUE);

		JPanel jp1 = new JPanel();
		jp1.setBackground(Color.WHITE);
		jp1.add(username);
		jp1.add(usernameBox);

		JPanel jp2 = new JPanel();
		jp2.setBackground(Color.WHITE);
		jp2.add(password);
		jp2.add(passwordBox);

		//Attempt To log in
		JButton checkLogin = new JButton("LOG IN");
		checkLogin.setBackground(Color.CYAN);
		checkLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String usernameText = usernameBox.getText();
				//String real-password = passwordBox.getText();
				String passwordText = encryption(passwordBox.getText());
				System.out.println(passwordText);
				boolean isTyped = checkTyped(usernameText, passwordText); // function to see if user put in information in the text and password fields
				boolean isUser = authenticate_user(usernameText, passwordText);
				if (isTyped && isUser){
					setVisible(false);
					mainframe = new FoodXFrame(cl, "user");
					mainframe.setVisible(true);

				}
				else{
					usernameBox.setText("");
					passwordBox.setText("");
				}
			}
		});

		//New User Button
		JButton newUserButton = new JButton("New User");
		newUserButton.setBackground(Color.CYAN);
		newUserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				nu.setVisible(true);	
			}
		});

		JButton guestButton = new JButton("Guest");
		guestButton.setBackground(Color.CYAN);
		guestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//boolean isUser = authenticate_user("guest", "Guest1");
				//if (isUser){
				setVisible(false);
				mainframe = new FoodXFrame(cl, "guest");
				mainframe.setVisible(true);
				//}
			}
		});

		//Set Bottom Panel
		Box login = Box.createVerticalBox();
		//login.add(picture);
		login.add(jp1);
		login.add(jp2);
		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		jp.add(login, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		jp.add(checkLogin, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		jp.add(newUserButton, gbc);
		gbc.gridx = 3;
		gbc.gridy = 0;
		jp.add(guestButton, gbc);
		add(jp, BorderLayout.SOUTH);

		//set Back ground For GUI
		GradientPanel gradientPanel = new GradientPanel();
		add(gradientPanel);
		setVisible(true);
	}

	public boolean checkTyped(String u, String p){
		if (u == null|| u.isEmpty() || p == null || p.isEmpty()){
			UIManager.put("OptionPane.background", Color.green);
			UIManager.put("Panel.background", Color.green);
			JOptionPane.showMessageDialog(null,
					"Both your userName and Password need to be filled out.... \n OR YOU WILL NOT EAT! ","WARNING",JOptionPane.WARNING_MESSAGE);	
			//JOptionPane.setBackground
			return false;
		}
		return true;
	}


	static class GradientPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int w = getWidth(); 
			int h = getHeight();
			Graphics2D g2d = (Graphics2D) g;
			g2d.setPaint(new GradientPaint(0, 0, Color.CYAN, 0, h, Color.WHITE));
			g2d.fillRect(0, 0, w, h);
		}
	}

	private String encryption(String p){
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] passBytes = p.getBytes();
			md.reset();
			byte[] digested = md.digest(passBytes);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<digested.length;i++){
				sb.append(Integer.toHexString(0xff & digested[i]));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("MD5 unavailable");
		}
		return null;
	}

	public boolean authenticate_user(String user, String password){
		String Authenticated = "Authenticated";
		String result = cl.authenticate_user(user, password);
		if (result.equals(Authenticated)){
			return true;
		}
		else{
			JOptionPane.showMessageDialog(null,
					result ,"Update",JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

	public String register_user(String user, String password){
		return null;
	}




}
