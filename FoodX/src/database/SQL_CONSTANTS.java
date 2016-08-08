package database;


public class SQL_CONSTANTS {

	public static final String DATABASE_NAME = "Fp_Foodx";
	public static final String FILE_TABLE = "userFile";
	public static final String FOLDER = "folder";
	public static final String INSERT = "INSERT INTO ";
	
	public static final String LOGIN_TABLE = "login";
	public static final String PASSWORD = "root";

	public static final String passWordField = "password";
	public static final String SELECT = "Select * FROM ";
	public static final String SHARED_NAME = "sharedFiles";
	public static final String SELECT_FROM_USER = "SELECT u.userName, f.userfile FROM " + LOGIN_TABLE + " u, " 
						+ SHARED_NAME +" f WHERE u.userID = f.userIdShared";
	
	public static final String USER = "root";
	public static final String USER_FILE = "userFile";
	public static final String USER_SHARED_ID = "userIDShared";
	
	public static final String userID = "userID";
	public static final String usernameField = "username";
	public static final String VALUES = "VALUES";
	
	
}
