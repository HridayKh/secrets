package utils;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpUtil {

	public static JSONObject readBodyJSON(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = req.getReader()) {
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject(sb.toString());
	}

	public static void sendJson(HttpServletResponse resp, int status, String type, String message) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		JSONObject json = new JSONObject();
		json.put("message", message);
		json.put("type", type);
		resp.getWriter().write(json.toString());
	}

}
