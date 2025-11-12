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

	/**
	 * Makes an HTTP request with the given method, URL, and optional body.
	 * Returns the response as a String. Throws IOException on error.
	 *
	 * @param method HTTP method (GET, POST, PUT, DELETE, etc.)
	 * @param urlStr The URL to request
	 * @param body   Optional request body (null for GET/DELETE)
	 * @return The response body as a String
	 * @throws IOException on network or protocol error
	 */
	public static String makeRequest(String method, String urlStr, String body) throws IOException {
		java.net.URL url = new java.net.URL(urlStr);
		java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Accept", "application/json");
		if (body != null && !body.isEmpty()) {
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			try (java.io.OutputStream os = conn.getOutputStream()) {
				byte[] input = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}
		}
		int status = conn.getResponseCode();
		java.io.InputStream is = (status >= 200 && status < 400) ? conn.getInputStream() : conn.getErrorStream();
		try (java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		} finally {
			conn.disconnect();
		}
	}

}
