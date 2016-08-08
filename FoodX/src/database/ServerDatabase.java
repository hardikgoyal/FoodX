package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ServerDatabase {
	private Connection conn = null;

	public ServerDatabase() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/" + SQL_CONSTANTS.DATABASE_NAME + "?user="
					+ SQL_CONSTANTS.USER + "&password=" + SQL_CONSTANTS.PASSWORD);
			System.out.println("Connected");

		} catch (ClassNotFoundException e) {
			System.out.println("CNFE: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQLE: " + e.getMessage());
		}
	}

	public String Authenticate_Login(String userName, String passWord) {
		PreparedStatement ps = null;
		String str = "Not Authenticated";
		try {
			ps = conn.prepareStatement(
					"Select * FROM " + SQL_CONSTANTS.LOGIN_TABLE + " WHERE " + SQL_CONSTANTS.usernameField + "=?");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String spassword = rs.getString(SQL_CONSTANTS.passWordField);
				if (passWord.compareTo(spassword) == 0){
					str = "Authenticated";
				}
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return str;
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean addUser(String user, String path) {
		PreparedStatement ps = null;
		boolean nexists = false;
		try{
			ps = conn.prepareStatement(SQL_CONSTANTS.SELECT_FROM_USER);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				String rsuser = rs.getString("userName");
				if (rs.getString("userfile").compareTo(path) == 0){
					if (rsuser.compareTo(user) == 0){
						nexists = true;
						JOptionPane.showMessageDialog(null,  "User Already Exists");
					}
				}
					
			}
			int id = getUserId(user);
			if (id == -1){
				JOptionPane.showMessageDialog(null, "Invalid User");
				nexists = false;
			}
			if (!nexists){
				ps = conn.prepareStatement(SQL_CONSTANTS.INSERT + SQL_CONSTANTS.SHARED_NAME + "(" 
						+ SQL_CONSTANTS.USER_FILE + "," + SQL_CONSTANTS.USER_SHARED_ID + ") VALUES (?,?)" );
				System.out.println("User Path :" +path + " user: " +  user);
				ps.setString(1, path);
				ps.setInt(2, id);
				ps.execute();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			
		}
		if (ps!=null){
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return !nexists;
	}

	public String getFolderName(String userName) {
		String un = "";
		PreparedStatement ps = null;
		try {

			// Get User ID
			ps = conn.prepareStatement(
					"Select * FROM" + SQL_CONSTANTS.LOGIN_TABLE + "WHERE " + SQL_CONSTANTS.usernameField + "=?");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			rs.next();
			int userId = rs.getInt(SQL_CONSTANTS.userID);
			rs.close();

			// Get User ID
			ps = conn.prepareStatement(
					"Select * FROM" + SQL_CONSTANTS.FILE_TABLE + "WHERE " + SQL_CONSTANTS.userID + "=?");
			ps.setInt(1, userId);
			rs = ps.executeQuery();
			rs.next();
			un = rs.getString(SQL_CONSTANTS.FOLDER);
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return un;
	}

	public int getnewId() {
		int id = 0;
		PreparedStatement ps = null;
		try {
			// Get User ID
			ps = conn.prepareStatement("Select * FROM " + SQL_CONSTANTS.LOGIN_TABLE);
			ResultSet rs = ps.executeQuery();

			if (rs.last()) {
				id = rs.getInt(SQL_CONSTANTS.userID);
				rs.close();
			}

			if (id == 0) {
				ps = conn.prepareStatement("ALTER TABLE " + SQL_CONSTANTS.LOGIN_TABLE + " AUTO_INCREMENT = 1");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return id + 1;
	}

	public String Register_User(String userName, String passWord) {
		System.out.println("In the Registered Interfface");
		boolean exists = false;
		PreparedStatement ps = null;
		String str = "User Already Exists";
		try {

			ps = conn.prepareStatement(
					SQL_CONSTANTS.SELECT + SQL_CONSTANTS.LOGIN_TABLE + " WHERE " + SQL_CONSTANTS.usernameField + "=?");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();

			while (rs.next() && !exists) {
				String user = rs.getString(SQL_CONSTANTS.usernameField);
				exists = (user.compareTo(userName) == 0);
			}

			if (!exists) {
				// Enter Data
				ps = conn.prepareStatement(SQL_CONSTANTS.INSERT + SQL_CONSTANTS.LOGIN_TABLE + "("
						+ SQL_CONSTANTS.usernameField + "," + SQL_CONSTANTS.passWordField + ") VALUES (?,?)");
				ps.setString(1, userName);
				ps.setString(2, passWord);
				ps.executeUpdate();
				System.out.println("I'm here");
				str = "Registered Successfully";
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		return str;
	}

	
	private int getUserId(String user) {
		PreparedStatement ps = null;
		try {
			// Get User ID
			String str ="SELECT * FROM " + SQL_CONSTANTS.LOGIN_TABLE;
			System.out.println(str);
			ps = conn.prepareStatement(str);
			ResultSet rs = ps.executeQuery();
			while (rs.next()){
				String username = rs.getString("userName");
				if (username.compareTo(user) == 0){
					return (rs.getInt("userID"));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}

	public void addLastZip(String user, String message) {
		try {
			PreparedStatement ps = conn.prepareStatement(SQL_CONSTANTS.INSERT + "userdetails" + "("
					+ "userID" + "," + "zip" + ") VALUES (?,?)");
			ps.setInt(1, getUserId(user));
			ps.setString(2, message);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getLastZip(String user) {
		String zip = "";
		try {
			PreparedStatement ps = conn.prepareStatement(SQL_CONSTANTS.SELECT + "userdetails Where userID =?");
			ps.setInt(1, getUserId(user));
			
			ResultSet rs = ps.executeQuery();
			if (rs.last())
			zip = rs.getString("zip");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zip;
		
	}

}
