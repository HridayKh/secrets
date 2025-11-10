package servlets.enviroments;

import db.EnvsDAO;
import db.ProjectsDAO;
import entities.Env;
import entities.Project;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import db.dbSecrets;
import utils.HttpUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class EnvList {
    private static final Logger log = LogManager.getLogger(EnvList.class);

    public static void listAllEnv(HttpServletRequest ignoredReq, HttpServletResponse resp, Map<String, String> params) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try (Connection conn = dbSecrets.getConnection()) {
           Project proj = ProjectsDAO.getProjectBySlug(conn, params.get("projectSlug"));
            if (proj == null) {
                HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Project not found.");
                return;
            }
            Env[] Envs = EnvsDAO.getAllEnvs(conn, proj.id);
            JSONObject envsJson = new JSONObject();
            envsJson.put("message", "Envs listed.");
            envsJson.put("type", "success");

            JSONArray EnvsArr = new JSONArray();
            for (Env env : Envs) {
                JSONObject pJson = new JSONObject();
                pJson.put("id", env.id);
                pJson.put("name", env.name);
                EnvsArr.put(pJson);
            }

            envsJson.put("Envs", EnvsArr);
            HttpUtil.sendJson(resp, HttpServletResponse.SC_OK, envsJson);
        } catch (SQLException e) {
            log.catching(e);
            HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Internal Server Error");
        }

    }

}
