package servlets.keys;

import db.ProjectsDAO;
import db.dbSecrets;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.HttpUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class KeyList {
	private static final Logger log = LogManager.getLogger(KeyList.class);

	public static void listAllKeys(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params) throws IOException {
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
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project not found.");
				return;
			}

			JSONArray KeysArr = new JSONArray();
			Map<String, String> keys = db.KeysDAO.getAllKeysForProject(conn, proj.id);
			for (String currKey : keys.keySet())
				KeysArr.put(new JSONObject().put("id", currKey).put("label", keys.get(currKey)));

			JSONObject envsJson = new JSONObject();
			envsJson.put("message", "API keys listed.");
			envsJson.put("type", "success");
			envsJson.put("apiKeys", KeysArr);

			HttpUtil.sendJson(resp, HttpServletResponse.SC_OK, envsJson);
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error");
		}
	}
}
