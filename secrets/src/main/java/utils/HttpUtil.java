package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpUtil {
	private static final Logger log = LogManager.getLogger(HttpUtil.class);

	public static JSONObject readBodyJSON(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = req.getReader()) {
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (IOException e) {
			log.catching(e);
		}
		return new JSONObject(sb.toString());
	}

	public static void sendSimpleJson(HttpServletResponse resp, int status, String type, String message) throws IOException {
		JSONObject json = new JSONObject();
		json.put("message", message);
		json.put("type", type);
		sendJson(resp, status, json);
	}

	public static void sendJson(HttpServletResponse resp, int status, JSONObject json) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		resp.getWriter().write(json.toString());
	}

}
