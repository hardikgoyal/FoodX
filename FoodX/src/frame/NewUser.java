package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

public class NewUser extends JFrame {
	private static final long serialVersionUID = 9183816558021947333L;
	private JTextField username1;
	private JTextField password1;
	private JTextField repeat1;
	private static MessageDigest md;
	static Client cl;
	private String usernameText = null;
	private String realpassword = null;
	private String passwordText = null;
	private String repeatText = null; 
	

	public NewUser(){
		setTitle("New User");
		
		setSize(640,480);
		setLocation(200,200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(Color.YELLOW);
		setResizable(false);
		cl = new Client();
		
		//GradientPanel gradientPanel = new GradientPanel();
		
		//Text and Image at Top of Frame
		ImageIcon image = new ImageIcon("resources/img/never_stop_eating4.png");
		JLabel label = new JLabel(image);
		add(label, BorderLayout.NORTH);
		
		JLabel username = new JLabel("USERNAME ");
		username1 = new JTextField(20);
		
		JLabel password = new JLabel("PASSWORD ");
		password1 = new JPasswordField(20);
		
		JLabel repeat = new JLabel("REPEAT       ");
		repeat1 = new JPasswordField(20);
		
		JButton loginButton = new JButton("CREATE NEW USER"); 
		JButton returnToLogin = new JButton("Return to Login Menu");
		
		Box un = Box.createHorizontalBox();
		un.add(username);
		un.add(username1);
		
		Box pw = Box.createHorizontalBox();
		pw.add(password);
		pw.add(password1);
		
		Box rp = Box.createHorizontalBox();
		rp.add(repeat);
		rp.add(repeat1);
		
		Box buttons = Box.createHorizontalBox();
		buttons.add(loginButton);
		buttons.add(returnToLogin);
		
		Box allboxes = Box.createVerticalBox();
		allboxes.add(un);
		allboxes.add(pw);
		allboxes.add(rp);
		//allboxes.add(loginButton);
		allboxes.add(buttons);
		JPanel signupPanel = new JPanel();
		signupPanel.setBackground(Color.GREEN);
		signupPanel.add(allboxes, BorderLayout.CENTER);
		add(signupPanel,BorderLayout.SOUTH);
		
		//button to try and create a new user
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String usernameText = username1.getText();
				String realpassword = password1.getText();
				String passwordText = encryption(password1.getText());
				String repeatText = encryption(repeat1.getText());
				boolean isSame = comparePasswords(passwordText, repeatText);
				if (isSame == false){
					displayWarning1();
				}
				else{
					boolean meetsCriteria = checkCriteria(realpassword);
					if (meetsCriteria == false){
						displayWarning2();
					}
					else{
						boolean canUse = register_user(usernameText, passwordText);
						if (canUse){
							AuthorizationPanel.AP.setVisible(false);
//								
						}else{
							username1.setText("");
							password1.setText("");
							repeat1.setText("");
						}
					}
				}		
			}
		});
		
		returnToLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				username1.setText("");
				password1.setText("");
				repeat1.setText("");
				usernameText = "";
				realpassword = "";
				passwordText = "";
				repeatText = "";
				AuthorizationPanel.nu.setVisible(false);
				AuthorizationPanel.AP.setVisible(true);	
			}
		});
		
		GradientPanel gradientPanel = new GradientPanel();
		add(gradientPanel);	
	}
	
	static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(); 
            int h = getHeight();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(new GradientPaint(0, 0, Color.YELLOW, 0, h, Color.GREEN));
            g2d.fillRect(0, 0, w, h);
        }
    }
	
	//function will parse through string, ensuring the string has atleast one letter and one Uppercase letter;
	private boolean checkCriteria(String a){
		int number = 0;
		int upperCase = 0;
		for (char c : a.toCharArray()){
			if (Character.isDigit(c)){
				number = number + 1;	
			}
			if (Character.isUpperCase(c)){
				upperCase = upperCase + 1;
			}
		}
		
		if (number > 0 && upperCase > 0){
			return true;
		}
		else return false;
	}

	//function to compare strings
	private boolean comparePasswords(String a, String b){
		if (a == null|| a.isEmpty()){
			return false;
		}
		if (b == null || b.isEmpty()){
			return false;
		}
		
		if (a.equals(b)){
			return true;
		}
		else return false;
	}
	
	//function exclaims that the passwords typed are not the two same words
	private void displayWarning1(){
		JOptionPane.showMessageDialog(null,
			    "The Two Passwords Do Not Match",
			    "Password Failed",
			    JOptionPane.WARNING_MESSAGE);
		password1.setText("");
		repeat1.setText("");
	}
	
	//function displays the warning that the string needs to have at-least 1 number and 1 uppercase number
	private void displayWarning2(){
		JOptionPane.showMessageDialog(null,
			    "Password must contain at least  "
			    + "1 number and 1 uppercase letter!",
			    "Sign-Up Failed",
			    JOptionPane.WARNING_MESSAGE);
		password1.setText("");
		repeat1.setText("");
	}
	
	public static boolean register_user(String user, String password){
		String register = "Registered";
		String result = cl.register_user(user, password);
		if (result.equals(register)){
			return true;
		}
		else{
		JOptionPane.showMessageDialog(null,
			  result ,"Update",JOptionPane.WARNING_MESSAGE);
		return false;
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
}
