package db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbSecrets {
	private static final Logger log = LogManager.getLogger(dbSecrets.class);
	public final static String DB_URL = "jdbc:mysql://db.HridayKh.in:3306/Secrets_DB";
	public static final String DB_USER = System.getenv("SECRETS_DB_USER");
	public static final String DB_PASS = System.getenv("SECRETS_DB_PASSWORD");

	public static final String FRONT_HOST = System.getenv("VITE_SECRETS_FRONTEND");
//	public static final String BACK_HOST = System.getenv("VITE_SECRETS_BACKEND");

//	public final static String PROD = System.getenv("VITE_PROD");

	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.catching(e);
		}
		return DriverManager.getConnection(dbSecrets.DB_URL, dbSecrets.DB_USER, dbSecrets.DB_PASS);
	}

}
