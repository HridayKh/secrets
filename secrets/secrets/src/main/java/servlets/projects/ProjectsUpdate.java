package servlets.projects;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import db.dbSecrets;
import utils.HttpUtil;

public class ProjectsUpdate {
	private static final Logger log = LogManager.getLogger(ProjectsUpdate.class);

	public static void updateProject(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params)
			throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection conn = dbSecrets.getConnection()) {

			String projSlug = params.get("projectSlug");

			JSONObject body = HttpUtil.readBodyJSON(req);
			String slug = body.optString("slug", null);
			String name = body.optString("name", null);
			String description = body.optString("description", null);

			if (slug == null || slug.isBlank())
				slug = null;
			if (name == null || name.isBlank())
				name = null;
			if (description == null || description.isBlank())
				description = null;

			boolean projUpdated = db.ProjectsDAO.updateProject(conn, projSlug, slug, name, description);

			if (projUpdated) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success", "Project Updated.");
				log.info("Project Updated.");
			} else {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
						"Unable to Update Project.");
				log.warn("Unable to Update Project.");
			}
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
					"Internal Server Error");
		}

	}

}
