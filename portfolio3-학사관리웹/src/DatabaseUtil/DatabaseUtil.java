package DatabaseUtil;
import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseUtil {
	public static Connection getConnection() {
		try {
			String dbURL="jdbc:mysql://localhost:3306/kwUcampus?serverTimezone=UTC";//jdbc:mysql://localhost:3306/kwUcampus?serverTimezone=UTC
			//jdbc:mysql://localhost:3306/jjjj1352?serverTimezone=UTC
			String dbID="root";//jjjj1352
			String dbPassword="qkrwlgy37";//qkrwlgy37@
			Class.forName("com.mysql.cj.jdbc.Driver");//com.mysql.cj.jdbc.Driver
			return DriverManager.getConnection(dbURL,dbID,dbPassword);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
