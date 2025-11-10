package servlets.secrets;

import db.EnvsDAO;
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

public class SecretsUpdate {
	private static final Logger log = LogManager.getLogger(SecretsUpdate.class);

	public static void updateSecret(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection conn = dbSecrets.getConnection()) {
			String projectSlug = params.get("projectSlug");
			if (projectSlug == null || projectSlug.isEmpty()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "projectSlug is missing.");
				return;
			}
			Project proj = db.ProjectsDAO.getProjectBySlug(conn, projectSlug);
			if (proj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project not found.");
				return;
			}

			String envName = params.get("env");
			if (envName == null || envName.isEmpty()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "env name in missing.");
				return;
			}
			Env env = EnvsDAO.getEnvByProjIdAndName(conn, proj.id, envName);
			if (env == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Environment not found.");
				return;
			}

			String key = params.get("key");
			if (key == null || key.isEmpty()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "key is missing.");
				return;
			}

			JSONObject body = HttpUtil.readBodyJSON(req);
			String value = body.getString("value");
			if (value == null || value.isEmpty()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "value not found in body json.");
				return;
			}

			boolean secretUpdated = db.SecretsDAO.updateSecret(conn, env.id, key, value);
			if (secretUpdated) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success", "Secret Updated.");
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Unable to Update Secret.");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error");
		}

	}

}
