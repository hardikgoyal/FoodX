package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.management.JMX;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
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
	static AuthorizationPanel AP;
	private static NewUser nu;
	static FoodXFrame xf;
	
	private static MessageDigest md;
	static Client cl;
	
	public AuthorizationPanel() {
		
		//instantiate the Main Authorization Panel
		super("Food X Authorization Panel");
		setSize(640,480);
		setLocation(200,200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		cl = new Client();
		
		//Interactive Options
		JLabel username = new JLabel("User Name");
		JLabel password = new JLabel("Password  ");
		JTextField usernameBox = new JTextField("BrianTheHunk", 20);
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
				String passwordText = encryption(passwordBox.getText());
				System.out.println(passwordText);
				boolean isTyped = checkTyped(usernameText, passwordText); // function to see if user put in information in the text and password fields
				boolean isUser = authenticate_user(usernameText, passwordText);
				if (isTyped && isUser){
					AP.setVisible(false);
					xf.setVisible(true);
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
				AP.setVisible(false);
				nu.setVisible(true);	
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
		//gbc.gridx = 0;
		//gbc.gridy = -1;
		//jp.add(picture);
		gbc.gridx = 1;
		gbc.gridy = 0;
		jp.add(checkLogin, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		jp.add(newUserButton, gbc);
		add(jp, BorderLayout.SOUTH);
		
		//set Back ground For GUI
		GradientPanel gradientPanel = new GradientPanel();
		add(gradientPanel);
		setVisible(true);
	}
	
	public boolean checkTyped(String u, String p){
		if (u == null|| u.isEmpty() || p == null || p.isEmpty()){
			
			
			UIManager UI=new UIManager();
			 UI.put("OptionPane.background", Color.green);
			 UI.put("Panel.background", Color.green);
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
	
	public static void main (String args []){
		System.out.println("Food XXX");
		AP = new AuthorizationPanel();
		
		nu = new NewUser();
		nu.setVisible(false);
		
		xf = new FoodXFrame();
		xf.setVisible(false);
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
	
public static boolean authenticate_user(String user, String password){
	String Authenticated = "Authenticated";
		String result = cl.authenticate(user, password);
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
