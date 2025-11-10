package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SecretsDAO {
//	private static final Logger log = LogManager.getLogger(ProjectsDAO.class);

	public static String[] getAllSecretKeysFromEnvId(Connection conn, int envId) throws SQLException {
		String sql = "SELECT `key` FROM `secrets` WHERE `environment_id` = ?";
		ArrayList<String> secretKeys = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, envId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					secretKeys.add(rs.getString("key"));
				}
			}
		}
		return secretKeys.toArray(new String[0]);
	}

	public static Map<String, String> getAllSecretKeyValuesFromEnvId(Connection conn, int envId) throws SQLException {
		String sql = "SELECT `key`, `value` FROM `secrets` WHERE `environment_id` = ?";
		Map<String, String> secretKeys = new HashMap<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, envId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					secretKeys.put(rs.getString("key"), rs.getString("value"));
				}
			}
		}
		return secretKeys;
	}

	public static boolean createSecret(Connection conn, int envId, String key, String value) throws SQLException {
		String sql = "INSERT INTO `secrets` (`environment_id`, `key`, `value`) VALUES (?, ?, ?);";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, envId);
			stmt.setString(2, key);
			stmt.setString(3, value);
			return stmt.executeUpdate() > 0;
		}
	}

	public static String getSecretValue(Connection conn, int envId, String key) throws SQLException {
		String sql = "SELECT `value` FROM `secrets` WHERE `environment_id` = ? AND `key` = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, envId);
			stmt.setString(2, key);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("value");
			} else {
				return null;
			}
		}
	}

	public static boolean updateSecret(Connection conn, int envId, String key, String value) throws SQLException {
		String sql = "UPDATE `secrets` SET `value` = ? WHERE `environment_id` = ? AND `key` = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, value);
			stmt.setInt(2, envId);
			stmt.setString(3, key);
			return stmt.executeUpdate() > 0;
		}
	}

	public static boolean deleteSecret(Connection conn, int envId, String key) throws SQLException {
		String sql = "DELETE FROM `secrets` WHERE `environment_id` = ? AND `key` = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, envId);
			stmt.setString(2, key);
			return stmt.executeUpdate() > 0;
		}
	}
}
