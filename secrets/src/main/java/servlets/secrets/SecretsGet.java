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

public class SecretsGet {
	private static final Logger log = LogManager.getLogger(SecretsGet.class);

	public static void getSecret(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params) throws IOException {
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

			String secretValue = db.SecretsDAO.getSecretValue(conn, env.id, key);
			if (secretValue != null) {
				JSONObject respJson = new JSONObject();
				respJson.put("key", key);
				respJson.put("value", secretValue);
				respJson.put("type", "success");
				respJson.put("message", "Secret Found.");
				HttpUtil.sendJson(resp, HttpServletResponse.SC_OK, respJson);
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Secret key is invalid.");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error");
		}

	}

}
