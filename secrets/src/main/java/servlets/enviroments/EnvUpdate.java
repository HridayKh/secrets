package servlets.enviroments;

import db.ProjectsDAO;
import entities.Env;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import db.dbSecrets;
import utils.HttpUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class EnvUpdate {
	private static final Logger log = LogManager.getLogger(EnvUpdate.class);

	public static void updateEnv(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params)
		throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection conn = dbSecrets.getConnection()) {

			// Get Project
			String projSlug = params.get("projectSlug");
			Project proj = ProjectsDAO.getProjectBySlug(conn, projSlug);
			if (proj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project Not Found.");
				log.warn("Project Not Found.");
				return;
			}

			// Get Current Env
			String currentEnvName = params.get("env");
			if (currentEnvName == null || currentEnvName.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "Environment not provided.");
				log.warn("Environment not provided.");
				return;
			}

			// Verify current env exists
			Env currentEnvObj = db.EnvsDAO.getEnvByProjIdAndName(conn, proj.id, currentEnvName);
			if (currentEnvObj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Environment Not Found.");
				log.warn("Environment Not Found.");
				return;
			}

			// Read new env name from request body
			JSONObject body = HttpUtil.readBodyJSON(req);
			String newEnvName = body.optString("name", null);
			if (newEnvName == null || newEnvName.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "Blank or No name provided.");
				log.warn("Blank or No name provided.");
				return;
			}

			// Update Env
			boolean envUpdated = db.EnvsDAO.updateEnv(conn, currentEnvObj.id, newEnvName);

			if (envUpdated) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success", "Environment Updated.");
				log.info("Environment Updated.");
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
					"Unable to Update Environment.");
				log.warn("Unable to Update Environment.");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
				"Internal Server Error");
		}

	}

}
