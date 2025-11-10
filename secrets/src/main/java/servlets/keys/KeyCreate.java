package servlets.keys;

import db.KeysDAO;
import db.ProjectsDAO;
import db.dbSecrets;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import utils.HttpUtil;
import utils.PassUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class KeyCreate {
	private static final Logger log = LogManager.getLogger(KeyCreate.class);

	public static void createKey(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		String projectSlug = params.get("projectSlug");
		if (projectSlug == null || projectSlug.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "projectSlug not Provided.");
			return;
		}
		JSONObject requestJson = HttpUtil.readBodyJSON(req);
		String keyLabel = requestJson.optString("label", null);
		if (keyLabel == null || keyLabel.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "Key label not Provided.");
			return;
		}
		try (Connection conn = dbSecrets.getConnection()) {
			Project proj = ProjectsDAO.getProjectBySlug(conn, projectSlug);
			if (proj == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project not found.");
				return;
			}
			String key = PassUtil.generateSecureKey();
			if (KeysDAO.generateKey(conn, proj.id, keyLabel, PassUtil.sha256Hash(key))) {
				JSONObject json = new JSONObject();
				json.put("message", "API key created. Save the plaintext now; it will not be shown again!");
				json.put("type", "success");
				json.put("key_plaintext", key);
				HttpUtil.sendJson(resp, HttpServletResponse.SC_CREATED, json);
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Unable to Create Key.");
			}
		} catch (SQLException | NoSuchAlgorithmException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error.");
		}

	}

}
