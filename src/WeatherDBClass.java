import java.sql.*;

public class WeatherDBClass
{
	
	static Connection conn = null;
	static Statement stmt = null;
	
	public static void init(){
		
		
		try{
			String userName = "root";
			String password = "12345";
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/weatherdb";
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connected");
			stmt = conn.createStatement();
			String query = "CREATE TABLE weather ( ID int  );";
			stmt.executeUpdate(query);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {	
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static int countRowsDb() {

		int i = 0;
		try{
			String userName = "root";
			String password = "12345";
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/weatherdb";
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			//System.out.println("Database connected");
			stmt = conn.createStatement();
			String query = "SELECT id FROM weather;";
			ResultSet rs = stmt.executeQuery(query);
			if(rs.last()) {
				i = rs.getRow();
			}
			
			return i;
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {	
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return i;
	}
	
	public static void removeFromDb(int id) {
		try{
			String userName = "root";
			String password = "12345";
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/weatherdb";
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			//System.out.println("Database connected");
			stmt = conn.createStatement();
			String query = "DELETE FROM weather WHERE id = " + id +";";
			stmt.executeUpdate(query);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {	
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void removeAllFromDb() {
		try{
			String userName = "root";
			String password = "12345";
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/weatherdb";
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			//System.out.println("Database connected");
			stmt = conn.createStatement();
			String query = "TRUNCATE TABLE weather;";
			stmt.executeUpdate(query);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {	
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String getRow(String type, int id) {

		String i = "";
		try{
			String userName = "root";
			String password = "12345";
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/weatherdb";
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			//System.out.println("Database connected");
			stmt = conn.createStatement();
			String query = "SELECT "+type+" FROM weather WHERE id = "+id+";";
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next())
				i = rs.getString(1);
			
			return i;
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {	
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return i;
	}
	
	public static void addToDb(String city, String temp, String weatherType, String description, String humidity, String wind, String pressure){
		try{
			String userName = "root";
			String password = "12345";
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/weatherdb";
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			//System.out.println("Database connected");
			stmt = conn.createStatement();
			String query = "INSERT INTO weather (city, temp, weatherType, description, humidity, wind, pressure) VALUES (\""+city+"\",\""+temp+"\",\""+weatherType+"\",\""+description+"\",\""+humidity+"\",\""+wind+"\",\""+pressure+"\");";
			stmt.executeUpdate(query);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {	
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	}
	
