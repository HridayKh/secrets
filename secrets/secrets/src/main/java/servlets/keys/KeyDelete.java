package servlets.keys;

import db.ProjectsDAO;
import db.dbSecrets;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.HttpUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class KeyDelete {
	private static final Logger log = LogManager.getLogger(KeyDelete.class);

	public static void deleteKey(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection conn = dbSecrets.getConnection()) {
			String projSlug = params.get("projectSlug");
			if (projSlug == null || projSlug.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "projectSlug Invalid.");
				return;
			}
			Project proj = ProjectsDAO.getProjectBySlug(conn, projSlug);
			if (proj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project Not Found.");
				return;
			}

			String keyId = params.get("keyId");
			if (keyId == null || keyId.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "keyId Invalid.");
				return;
			}

			Map<String, String> keys = db.KeysDAO.getAllKeysForProject(conn, proj.id);
			for (String currKey : keys.keySet()) {
				if (currKey.equals(keyId)) {
					if (db.KeysDAO.revokeKey(conn, Integer.parseInt(currKey)))
						HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success", "API key revoked.");
					else
						HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Unable to Delete API Key.");
					return;
				}
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error");
		}

	}

}
