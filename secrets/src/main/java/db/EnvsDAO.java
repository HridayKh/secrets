package db;

import entities.Env;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EnvsDAO {
//    private static final Logger log = LogManager.getLogger(EnvsDAO.class);

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



//	public static boolean createProject(Connection conn, String slug, String name, String description)
//			throws SQLException {
//		String sql = "INSERT INTO `projects` (`slug`, `name`, `description`) VALUES (?, ?, ?);";
//
//		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//			stmt.setString(1, slug);
//			stmt.setString(2, name);
//			stmt.setString(3, description);
//			return stmt.executeUpdate() > 0;
//		}
//	}
//
//	public static boolean updateProject(Connection conn, String projSlug, String slug, String name, String description)
//			throws SQLException {
//		String sql = "UPDATE `projects` SET ";
//		boolean fieldAdded = false;
//		if (slug != null) {
//			sql += "`slug`=?";
//			fieldAdded = true;
//		}
//		if (name != null) {
//			sql += fieldAdded ? ", " : "";
//			sql += "`name`=?";
//			fieldAdded = true;
//		}
//		if (description != null) {
//			sql += fieldAdded ? ", " : "";
//			sql += "`description`=?";
//			fieldAdded = true;
//		}
//		if (!fieldAdded)
//			return true;
//		sql += " WHERE slug=?";
//		log.info("DEBUG SQL: " + sql);
//		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//			int paramIndex = 1;
//			if (slug != null) {
//				stmt.setString(paramIndex++, slug);
//			}
//			if (name != null) {
//				stmt.setString(paramIndex++, name);
//			}
//			if (description != null) {
//				stmt.setString(paramIndex++, description);
//			}
//			stmt.setString(paramIndex++, projSlug);
//			return stmt.executeUpdate() > 0;
//		}
//	}

}
