package servlets.enviroments;

import db.ProjectsDAO;
import entities.Env;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import db.dbSecrets;
import utils.HttpUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class EnvDelete {
	private static final Logger log = LogManager.getLogger(EnvDelete.class);

	public static void deleteEnv(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params)
		throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection conn = dbSecrets.getConnection()) {

			// Get Project
			String projSlug = params.get("projectSlug");
			Project proj = ProjectsDAO.getProjectBySlug(conn, projSlug);
			if (proj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project Not Found.");
				log.info("Project Not Found.");
				return;
			}

			// Get Env
			String envName = params.get("env");
			if (envName == null || envName.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "Environment not provided.");
				log.info("Environment not provided.");
				return;
			}

			// Verify env exists
			Env currentEnvObj = db.EnvsDAO.getEnvByProjIdAndName(conn, proj.id, envName);
			if (currentEnvObj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Environment Not Found.");
				log.info("Environment Not Found.");
				return;
			}

			// Delete Env
			boolean envDeleted = db.EnvsDAO.deleteEnv(conn, currentEnvObj.id);

			if (envDeleted) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success", "Environment Deleted.");
				log.info("Environment Deleted.");
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
					"Unable to Deleted Environment.");
				log.warn("Unable to Deleted Environment.");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
				"Internal Server Error");
		}

	}

}
