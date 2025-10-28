package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import entities.Project;

public class ProjectsDAO {
//	private static final Logger log = LogManager.getLogger(ProjectsDAO.class);

	public static Project[] getAllProjects(Connection conn) throws SQLException {
		String sql = "SELECT * FROM projects";
		ArrayList<Project> projects = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					projects.add(Project.parseProject(rs));
				}
			}
		}
		return projects.toArray(new Project[0]);
	}

	public static boolean createProject(Connection conn, String slug, String name, String description)
			throws SQLException {
		String sql = "INSERT INTO `projects` (`slug`, `name`, `description`) VALUES (?, ?, ?);";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, slug);
			stmt.setString(2, name);
			stmt.setString(3, description);
			return stmt.executeUpdate() > 0;
		}
	}

	public static boolean updateProject(Connection conn, String projSlug, String slug, String name, String description)
			throws SQLException {
		String sql = "UPDATE `projects` SET ";
		boolean fieldAdded = false;
		if (slug != null) {
			sql += "`slug`=?";
			fieldAdded = true;
		}
		if (name != null) {
			sql += fieldAdded ? ", " : "";
			sql += "`name`=?";
			fieldAdded = true;
		}
		if (description != null) {
			sql += fieldAdded ? ", " : "";
			sql += "`description`=?";
			fieldAdded = true;
		}
		if (!fieldAdded)
			return true;
		sql += " WHERE slug=?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			int paramIndex = 1;
			if (slug != null) {
				stmt.setString(paramIndex++, slug);
			}
			if (name != null) {
				stmt.setString(paramIndex++, name);
			}
			if (description != null) {
				stmt.setString(paramIndex++, description);
			}
			stmt.setString(paramIndex, projSlug);
			return stmt.executeUpdate() > 0;
		}
	}

    public static Project getProjectBySlug(Connection conn, String slug) throws SQLException {
        String sql = "SELECT * FROM projects WHERE slug=?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Project.parseProject(rs);
                } else {
                    return null;
                }
            }
        }
    }
}
