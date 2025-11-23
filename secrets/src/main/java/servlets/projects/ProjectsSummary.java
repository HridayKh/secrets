package servlets.projects;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import entities.Env;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import db.EnvsDAO;
import db.ProjectsDAO;
import db.SecretsDAO;
import db.dbSecrets;
import utils.HttpUtil;

public class ProjectsSummary {
	private static final Logger log = LogManager.getLogger(ProjectsSummary.class);

	public static void getProjectSummary(HttpServletRequest ignoredReq, HttpServletResponse resp,
			Map<String, String> pathParams) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String projectSlug = pathParams.get("projectSlug");
		if (projectSlug == null || projectSlug.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error",
					"projectSlug Invalid.");
			return;
		}

		try (Connection conn = dbSecrets.getConnection()) {
			Project project = ProjectsDAO.getProjectBySlug(conn, projectSlug);
			if (project == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error",
						"Project Not Found.");
				return;
			}

			JSONObject projectsJson = new JSONObject();
			projectsJson.put("id", project.id);
			projectsJson.put("slug", project.slug);
			projectsJson.put("name", project.name);
			projectsJson.put("description", project.description);
			projectsJson.put("message", "Project summary fetched successfully.");
			projectsJson.put("type", "success");

			Env[] envs = EnvsDAO.getAllEnvs(conn, project.id);
			JSONArray envsJson = new JSONArray();
			for (Env env : envs)
				envsJson.put(new JSONObject()
						.put("name", env.name)
						.put("secrets", SecretsDAO.getAllSecretKeysFromEnvId(conn, env.id)));
			projectsJson.put("envs", envsJson);

			HttpUtil.sendJson(resp, HttpServletResponse.SC_OK, projectsJson);
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
					"Internal Server Error");
		}

	}

}
