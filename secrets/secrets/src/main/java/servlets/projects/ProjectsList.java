package servlets.projects;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import db.dbSecrets;
import utils.HttpUtil;

public class ProjectsList {
	private static final Logger log = LogManager.getLogger(ProjectsList.class);

	public static void listAllProjects(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> ignoredParams) throws IOException  {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		try (Connection conn = dbSecrets.getConnection()) {
			Project[] projects = db.ProjectsDAO.getAllProjects(conn);
			JSONObject projectsJson = new JSONObject();
			projectsJson.put("message", "Projects listed.");
			projectsJson.put("type", "success");
			
			JSONArray projectsArr = new JSONArray();
			for(Project p : projects) {
				JSONObject pJson = new JSONObject();
				pJson.put("id", p.id);
				pJson.put("slug", p.slug);
				pJson.put("name", p.name);
				pJson.put("description", p.description);
				projectsArr.put(pJson);
			}
			
			projectsJson.put("projects", projectsArr);
			HttpUtil.sendJson(resp, HttpServletResponse.SC_OK, projectsJson);
		} catch (SQLException e) {
			log.catching(e);
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error");
		}
		
	}

}
