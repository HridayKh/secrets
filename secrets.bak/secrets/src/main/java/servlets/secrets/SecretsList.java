package servlets.secrets;

import db.EnvsDAO;
import entities.Env;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import db.dbSecrets;
import utils.HttpUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class SecretsList {
	private static final Logger log = LogManager.getLogger(SecretsList.class);

	public static void listAllSecrets(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection conn = dbSecrets.getConnection()) {
			String projectSlug = params.get("projectSlug");
			String envName = params.get("env");
			if (projectSlug == null || projectSlug.isEmpty()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "projectSlug is missing.");
				return;
			}
			if (envName == null || envName.isEmpty()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "env name in missing.");
				return;
			}

			Project proj = db.ProjectsDAO.getProjectBySlug(conn, projectSlug);
			if (proj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project not found.");
				return;
			}

			Env env = EnvsDAO.getEnvByProjIdAndName(conn, proj.id, envName);
			if (env == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Environment not found.");
				return;
			}

			String[] secretKeys = db.SecretsDAO.getAllSecretKeysFromEnvId(conn, env.id);
			JSONArray secretsKeysAsJson = new JSONArray(secretKeys);

			JSONObject secretsListJson = new JSONObject();
			secretsListJson.put("secrets", secretsKeysAsJson);
			secretsListJson.put("message", "Secrets' Keys Listed.");
			secretsListJson.put("type", "success");

			HttpUtil.sendJson(resp, HttpServletResponse.SC_OK, secretsListJson);
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error");
		}

	}

}
