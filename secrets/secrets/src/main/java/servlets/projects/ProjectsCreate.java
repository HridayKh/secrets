package servlets.projects;

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

public class ProjectsCreate {
	private static final Logger log = LogManager.getLogger(ProjectsCreate.class);

	public static void createProject(HttpServletRequest req, HttpServletResponse resp, Map<String, String> ignoredParams)
		throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection conn = dbSecrets.getConnection()) {

			JSONObject body = HttpUtil.readBodyJSON(req);
			String slug = body.optString("slug", null);
			String name = body.optString("name", null);
			String description = body.optString("description", null);

			if (slug == null || slug.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error",
					"Project Creation slug not Provided.");
				return;
			}

			if (name == null || name.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error",
					"Project Creation name not Provided.");
				return;
			}

			if (description == null || description.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error",
					"Project Creation description not Provided.");
				return;
			}

			boolean projCreated = db.ProjectsDAO.createProject(conn, slug, name, description);

			if (projCreated) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_CREATED, "success", "Project Created.");
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
					"Unable to Create Project.");
				log.warn("Unable to Create Project.");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
				"Internal Server Error");
		}

	}

}
