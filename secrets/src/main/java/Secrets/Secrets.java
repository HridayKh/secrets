package Secrets;

public class Secrets {
	public final static String DB_URL = "jdbc:mysql://db.hridaykh.in:3306/Secrets_DB";
	public static final String DB_USER = System.getenv("SECRETS_DB_USER");
	public static final String DB_PASS = System.getenv("SECRETS_DB_PASSWORD");
	
	public static final String FRONT_HOST = System.getenv("VITE_SECRETS_FRONTEND");
	public static final String BACK_HOST = System.getenv("VITE_SECRETS_BACKEND");
	
	public final static String PROD = System.getenv("VITE_PROD");
}
