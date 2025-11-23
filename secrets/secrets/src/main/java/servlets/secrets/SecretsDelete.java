package servlets.secrets;

import db.EnvsDAO;
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

public class SecretsDelete {
	private static final Logger log = LogManager.getLogger(SecretsDelete.class);

	public static void deleteSecret(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params)
		throws IOException {
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

			boolean secretDeleted = db.SecretsDAO.deleteSecret(conn, env.id, key);

			if (secretDeleted) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success", "Secret Deleted.");
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
					"Unable to Delete Secret.");
				log.warn("Unable to Delete Secret");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
				"Internal Server Error");
		}

	}

}
