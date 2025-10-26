package db;

import entities.Env;
import entities.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class EnvsDAO {
//	private static final Logger log = LogManager.getLogger(EnvsDAO.class);

	public static Env[] getAllEnvs(Connection conn, int projId) throws SQLException {
		String sql = "SELECT * FROM environments where project_id=?";
		ArrayList<Env> envs = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, projId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					envs.add(Env.parseEnv(rs));
				}
			}
		}
		return envs.toArray(new Env[0]);
	}


	public static boolean createEnv(Connection conn, String projSlug, String envName)
		throws SQLException {
		Project proj = ProjectsDAO.getProjectBySlug(conn, projSlug);
		if (proj == null)
			return false;

		String sql = "INSERT INTO `environments` (`project_id`, `name`) VALUES (?, ?);";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, proj.id);
			stmt.setString(2, envName);
			return stmt.executeUpdate() > 0;
		}
	}

	public static boolean updateEnv(Connection conn, int envId, String newEnvName)
		throws SQLException {
		String sql = "UPDATE `environments` SET `name`=? WHERE id=?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, newEnvName);
			stmt.setInt(2, envId);
			return stmt.executeUpdate() > 0;
		}
	}

	public static boolean deleteEnv(Connection conn, int envId)
		throws SQLException {
		String sql = "DELETE FROM `environments` WHERE id=?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, envId);
			return stmt.executeUpdate() > 0;
		}
	}

	public static Env getEnvByProjIdAndName(Connection conn, int projId, String envName) throws SQLException {
		String sql = "SELECT * FROM environments WHERE project_id=? AND name=?;";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, projId);
			stmt.setString(2, envName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Env.parseEnv(rs);
				} else {
					return null;
				}
			}
		}
	}

}
