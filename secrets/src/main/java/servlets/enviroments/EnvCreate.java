package servlets.enviroments;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import secrets.Secrets;
import utils.HttpUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class EnvCreate {
	private static final Logger log = LogManager.getLogger(EnvCreate.class);

	public static void createEnv(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String projectSlug = params.get("projectSlug");
		String envName = params.get("env");

		if (projectSlug == null || projectSlug.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "Environment Creation projectSlug not Provided.");
			log.info("Environment Creation projectSlug not Provided.");
			return;
		}

		if (envName == null || envName.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "Environment Creation env name not Provided.");
			log.info("Environment Creation env name not Provided.");
			return;
		}

		try (Connection conn = Secrets.getConnection()) {

			boolean envCreated = db.EnvsDAO.createEnv(conn, projectSlug, envName);

			if (envCreated) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success", "Environment Created.");
				log.info("Environment Created.");
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Unable to Create Environment.");
				log.warn("Unable to Create Environment.");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error.");
		}

	}

}
