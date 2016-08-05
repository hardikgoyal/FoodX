package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class importcsv {
	public static void main(String args[]) throws IOException{
		BufferedReader reader = Files.newBufferedReader(Paths.get("resources/zipcode.csv"));
		String str = "";
		str=reader.readLine();
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/FP_FOODX?user=root&password=root");
			System.out.println("Connected");
		} catch (ClassNotFoundException e) {
			System.out.println("CNFE: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQLE: " + e.getMessage());
		}
		PreparedStatement  ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO ZipcodeLatitude Values (?, ?, ?, ?, ?,? )");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int i = 0;
		while ((str= reader.readLine())!=null){
			i++;
			String line[] = str.split(",");
			if (line.length<5){
				continue;
			}
			for (int l =0; l<6; l++){
				line[l] = line[l].replaceAll("\"", "");
			}

			try {
				ps.setString(1, line[0]);
				ps.setString(2, line[1]);
				ps.setString(3, line[2]);
				ps.setFloat(4, Float.parseFloat(line[3]));
				ps.setFloat(5, Float.parseFloat(line[4]));
				ps.setInt(6, Integer.parseInt(line[5]));
				ps.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(i);
	}
}
