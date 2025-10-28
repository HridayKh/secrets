package utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * A utility class for securely encrypting and decrypting strings using AES-256 in GCM mode.
 * <p>
 * This implementation follows the recommended pattern for storing secrets:
 * 1. Generate a unique Initialization Vector (IV/Nonce) for every encryption.
 * 2. Encrypt the plaintext using the Master Encryption Key (MEK) and the IV.
 * 3. Concatenate the IV and the resulting Ciphertext (which includes the GCM Authentication Tag).
 * 4. Base64-encode the combined byte array for safe storage in a MEDIUMTEXT column.
 */
public class EncryptUtil {

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int GCM_IV_LENGTH = 12; // 96 bits - standard for GCM
	private static final int GCM_TAG_LENGTH = 16; // 128 bits - standard for GCM
	private static final int AES_KEY_LENGTH_BYTES = 32; // 256 bits

	/**
	 * Generates a secure, random 256-bit (32 byte) AES Master Key.
	 * This key should be generated once and stored securely outside the database.
	 *
	 * @return A SecretKey object representing the Master Encryption Key.
	 * @throws Exception if key generation fails.
	 */
	public static SecretKey generateMasterKey() throws Exception {
		// Use KeyGenerator for proper key generation
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // 256 bits for AES-256
		return keyGen.generateKey();
	}

	/**
	 * Converts a SecretKey object into a Base64-encoded string for secure storage.
	 * This is the function you use to get the text format for storage (e.g., in an environment variable).
	 *
	 * @param key The SecretKey to export.
	 * @return The Base64 representation of the 32-byte key.
	 */
	public static String exportKeyToBase64(SecretKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * Reconstructs a SecretKey object from a Base64-encoded string.
	 * This is the function you use to load the key from your secure storage source.
	 *
	 * @param base64Key The Base64 string of the key, typically loaded from an environment variable or KMS.
	 * @return The SecretKey object.
	 */
	public static SecretKey loadKeyFromBase64(String base64Key) {
		if (base64Key == null || base64Key.isEmpty()) {
			throw new IllegalArgumentException("Master Key Base64 string cannot be null or empty.");
		}
		byte[] keyBytes = Base64.getDecoder().decode(base64Key);

		// Security check: ensure the key is the correct size
		if (keyBytes.length != AES_KEY_LENGTH_BYTES) {
			throw new IllegalArgumentException("Master Key must be 32 bytes (256 bits), but found " + keyBytes.length + " bytes.");
		}

		return new SecretKeySpec(keyBytes, "AES");
	}

	/**
	 * Encrypts the plaintext using the provided Master Encryption Key.
	 * The output is a Base64-encoded string combining the IV and Ciphertext.
	 *
	 * @param plaintext The sensitive data to be encrypted.
	 * @param key       The 256-bit Master Encryption Key (MEK).
	 * @return The Base64-encoded string ready for database storage (IV + Ciphertext + AuthTag).
	 * @throws Exception if encryption fails.
	 */
	public static String encrypt(String plaintext, SecretKey key) throws Exception {
		// 1. Generate a unique, random IV (Nonce) for this operation
		byte[] iv = new byte[GCM_IV_LENGTH];
		(new SecureRandom()).nextBytes(iv);

		// 2. Initialize Cipher
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);

		// 3. Encrypt the data
		byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));

		// 4. Combine IV and Ciphertext for storage
		// The GCM tag is appended to the ciphertext automatically by the cipher.
		ByteBuffer buffer = ByteBuffer.allocate(iv.length + ciphertext.length);
		buffer.put(iv);
		buffer.put(ciphertext);

		// 5. Base64 encode the combined result
		return Base64.getEncoder().encodeToString(buffer.array());
	}

	/**
	 * Decrypts the Base64-encoded string using the Master Encryption Key.
	 *
	 * @param ciphertextBase64 The Base64-encoded string retrieved from the database.
	 * @param key              The 256-bit Master Encryption Key (MEK).
	 * @return The original decrypted plaintext string.
	 * @throws Exception if decryption fails (e.g., incorrect key, tampered data).
	 */
	public static String decrypt(String ciphertextBase64, SecretKey key) throws Exception {
		// 1. Base64 decode the stored string
		byte[] combined = Base64.getDecoder().decode(ciphertextBase64);

		// Check if the data is long enough to contain the IV
		if (combined.length < GCM_IV_LENGTH) {
			throw new IllegalArgumentException("Stored ciphertext is too short.");
		}

		// 2. Separate IV and Ciphertext
		// The first 12 bytes are the IV
		byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
		// The rest is the Ciphertext + AuthTag
		byte[] ciphertextWithTag = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);

		// 3. Initialize Cipher for decryption
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
		cipher.init(Cipher.DECRYPT_MODE, key, spec);

		// 4. Decrypt the data
		// If the data was tampered with or the key is wrong, this line will throw a BadPaddingException
		byte[] plaintextBytes = cipher.doFinal(ciphertextWithTag);

		return new String(plaintextBytes, "UTF-8");
	}

	public static void main(String[] args) {
		try {
			// --- SIMULATED DEPLOYMENT WORKFLOW ---

			// PHASE 1: KEY GENERATION & EXPORT (Run once, then store the string securely)
			System.out.println("--- PHASE 1: GENERATION AND EXPORT ---");
			SecretKey generatedKey = generateMasterKey();
			String exportedKeyString = exportKeyToBase64(generatedKey);

			System.out.println("Generated Master Key (Binary): ");
			System.out.println("Exported Base64 Key (Store this securely!):");
			System.out.println(exportedKeyString);

			// PHASE 2: APPLICATION STARTUP (Load the key from secure storage)
			System.out.println("\n--- PHASE 2: APPLICATION STARTUP & KEY LOAD ---");
			// Simulate loading the key from a secure source (like System.getenv("MASTER_KEY"))
			SecretKey loadedKey = loadKeyFromBase64(exportedKeyString);
			System.out.println("Key successfully loaded from Base64 string.");

			// --- 3. The Secret Value ---
			String originalSecret = "db_user_password_2025_prod_aBc123XyZ";
			System.out.println("\nOriginal Plaintext Secret: " + originalSecret);

			// --- 4. Encryption (Using the loaded key) ---
			String encryptedSecret = encrypt(originalSecret, loadedKey);
			System.out.println("Encrypted (Base64-encoded) for DB storage:");
			System.out.println(encryptedSecret);

			// --- 5. Decryption (Using the loaded key) ---
			String decryptedSecret = decrypt(encryptedSecret, loadedKey);
			System.out.println("\nDecrypted Plaintext Secret: " + decryptedSecret);

			// --- 6. Verification ---
			if (originalSecret.equals(decryptedSecret)) {
				System.out.println("\nSUCCESS: Original and Decrypted values match using the loaded key.");
			} else {
				System.err.println("\nERROR: Decryption failed.");
			}

		} catch (Exception e) {
			System.err.println("An error occurred during cryptography operations:");
			e.printStackTrace();
		}
	}
}
