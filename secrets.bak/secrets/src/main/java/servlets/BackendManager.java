package servlets;

import entities.Env;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utils.HttpUtil;
import utils.PassUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

public class BackendManager {

/*
1. get header
	- if empty, return 401
	- else, get project associated with the key
		- if no project found, return 401
		- else check if projectSlug in url matches projectSlug from api key
			- if not match, return 403

2. get env from url
	- if env not found in project, return 404

3. if getting all secrets
	- return all secrets for that env
*/

	public static void getAllSecrets(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params) throws IOException {
		String authHeader = req.getHeader("Authorization");
		if (authHeader == null || authHeader.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "error", "Missing API Key.");
			return;
		}

		String apiKey = authHeader.split("Bearer ")[1];
		if (apiKey == null || apiKey.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "error", "Missing API Key.");
			return;
		}

		try (var conn = db.dbSecrets.getConnection()) {

			int projectId = db.KeysDAO.getProjectIdFromKeyHash(conn, PassUtil.sha256Hash(apiKey));
			if (projectId == 0) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "error", "Invalid API Key.");
				return;
			}

			String envName = params.get("env");
			if(envName == null || envName.isBlank()) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_BAD_REQUEST, "error", "Missing environment name.");
				return;
			}

			Env env = db.EnvsDAO.getEnvByProjIdAndName(conn, projectId,  envName);
			if(env == null) {
				HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_NOT_FOUND, "error", "Environment not found.");
				return;
			}
			Map<String, String> secrets = db.SecretsDAO.getAllSecretKeyValuesFromEnvId(conn, env.id);
			HttpUtil.sendJson(resp, HttpServletResponse.SC_OK, new JSONObject(secrets));
		} catch (SQLException | NoSuchAlgorithmException e) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error", "Unable to get secrets.");
		}
	}

}
