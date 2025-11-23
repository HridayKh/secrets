package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class KeysDAO {
//	private static final Logger log = LogManager.getLogger(EnvsDAO.class);

	public static Map<String, String> getAllKeysForProject(Connection conn, int projId) throws SQLException {
		String sql = "SELECT * FROM `api_keys` where `project_id` = ?";
		Map<String, String> keys = new HashMap<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, projId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					keys.put(String.valueOf(rs.getInt("id")), rs.getString("label"));
				}
			}
		}
		return keys;
	}

	public static boolean generateKey(Connection conn, int projId, String label, String keyHash) throws SQLException {
		String sql = "INSERT INTO `api_keys` (`project_id`, `label`, `key_hash`) VALUES (?, ?, ?);";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, projId);
			stmt.setString(2, label);
			stmt.setString(3, keyHash);
			return stmt.executeUpdate() > 0;
		}
	}

	public static boolean revokeKey(Connection conn, int keyId) throws SQLException {
		String sql = "DELETE FROM `api_keys` WHERE `id` = ?;";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, keyId);
			return stmt.executeUpdate() > 0;
		}
	}

	public static int getProjectIdFromKeyHash(Connection conn, String keyHash) throws SQLException {
		String sql = "SELECT * FROM `api_keys` where `key_hash` = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, keyHash);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("project_id");
				}
			}
		}
		return 0;
	}

}
