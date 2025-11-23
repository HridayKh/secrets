package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class dbSecrets {
	public final static String DB_URL = "jdbc:mysql://db.HridayKh.in:3306/Secrets_DB";
	public static final String DB_USER = System.getenv("SECRETS_DB_USER");
	public static final String DB_PASS = System.getenv("SECRETS_DB_PASSWORD");

	public static final String FRONT_HOST = System.getenv("VITE_SECRETS_FRONTEND");
	// public static final String BACK_HOST = System.getenv("VITE_SECRETS_BACKEND");

	// public final static String PROD = System.getenv("VITE_PROD");

	private static final HikariDataSource dataSource;
	static {
		HikariConfig config = new HikariConfig();
		// config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
		config.setDriverClassName("com.mysql.cj.jdbc.Driver");
		config.setJdbcUrl(DB_URL);
		config.setUsername(DB_USER);
		config.setPassword(DB_PASS);

		config.setMaximumPoolSize(10);
		config.setMinimumIdle(5);

		config.setPoolName("SecretsHikariCP");
		dataSource = new HikariDataSource(config);
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}
