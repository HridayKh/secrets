package utils;

import db.dbSecrets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PassUtil {

	private static final String PREFIX = "hkscrt_";
	private static final int KEY_LENGTH_BYTES = 32;

	public static String generateSecureKey() {
		byte[] keyBytes = new byte[KEY_LENGTH_BYTES];
		new SecureRandom().nextBytes(keyBytes);
		String encodedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
		return PREFIX + encodedKey;
	}

	public static String sha256Hash(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = md.digest(password.getBytes());
		StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
		for (byte b : hashBytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String signString(String uuid) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac hmac = Mac.getInstance("HmacSHA256");
		SecretKeySpec keySpec = new SecretKeySpec(sha256Hash(dbSecrets.DB_PASS).getBytes(), "HmacSHA256");
		hmac.init(keySpec);
		byte[] rawHmac = hmac.doFinal(uuid.getBytes());
		return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
	}
}
