package secrets;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import utils.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class AuthFilter implements Filter {

	private static final Logger log = LogManager.getLogger(AuthFilter.class);
	private static final String AUTH_COOKIE_NAME = "hridaykh_in_auth_token";
	private static final String AUTH_ENDPOINT = System.getenv("VITE_AUTH_BACKEND") + "/v1/users/me";
	private static final String AUTH_KEY_HEADER = "X-HridayKh-In-Auth-Key";
	private static final String AUTH_KEY_VALUE = System.getenv("BACKEND_API_KEYS");

	@Override
	public void init(FilterConfig filterConfig) {
		log.info("Auth Filter Created.");
	}

	@Override
	public void destroy() {
		log.info("Auth Filter Destroyed.");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		Cookie[] cookies = req.getCookies();
		if (cookies == null) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "error",
					"Unauthorized: Not logged in secrets!");
			return;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(AUTH_COOKIE_NAME)) {
				try {
					if (handleAuthCookie(cookie, resp, chain, req))
						chain.doFilter(servletRequest, servletResponse);
					return;
				} catch (IOException | ServletException | URISyntaxException e) {
					log.error("Auth endpoint URL error", e);
					HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"error", "Internal server error");
					return;
				}
			}
		}
		HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "error",
				"Unauthorized: Not logged in secrets!");
	}

	private boolean handleAuthCookie(Cookie cookie, HttpServletResponse resp, FilterChain chain,
			HttpServletRequest req) throws IOException, ServletException, URISyntaxException {
		URL url = new URI(AUTH_ENDPOINT).toURL();
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Cookie", AUTH_COOKIE_NAME + "=" + cookie.getValue());

		if (AUTH_KEY_VALUE == null || AUTH_KEY_VALUE.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error",
					"Internal server error (hint: key)");
			return false;
		}

		String firstApiKey = AUTH_KEY_VALUE.split(",")[0].trim();
		if (firstApiKey == null || firstApiKey.isBlank()) {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"error", "Internal server error (hint: key)");
			return false;
		}

		con.setRequestProperty(AUTH_KEY_HEADER, firstApiKey);
		int status = con.getResponseCode();
		StringBuilder responseBody = new StringBuilder();

		if (status < 200 || status >= 300) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
				String line;
				while ((line = br.readLine()) != null) {
					responseBody.append(line);
				}
			}
			JSONObject jsonResponse = new JSONObject(responseBody.toString());
			HttpUtil.sendSimpleJson(resp, status, jsonResponse.optString("type", "error"),
					jsonResponse.optString("message", "Unauthorized!"));
			return false;
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String line;
			while ((line = br.readLine()) != null) {
				responseBody.append(line);
			}
		}
		JSONObject jsonResponse = new JSONObject(responseBody.toString());
		JSONObject perms = jsonResponse.optJSONObject("permissions", new JSONObject());

		if (!perms.optBoolean("allowSecretsAccess", false)) {
			HttpUtil.sendSimpleJson(resp, 403, "error",
					"Not authorized to access this app!");
			return false;
		}
		return true;

	}
}
