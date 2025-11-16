// java
package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import secrets.TestServlet;
import servlets.enviroments.EnvCreate;
import servlets.enviroments.EnvDelete;
import servlets.enviroments.EnvList;
import servlets.enviroments.EnvUpdate;
import servlets.keys.KeyCreate;
import servlets.keys.KeyDelete;
import servlets.keys.KeyList;
import servlets.projects.ProjectsCreate;
import servlets.projects.ProjectsList;
import servlets.projects.ProjectsSummary;
import servlets.projects.ProjectsUpdate;
import servlets.secrets.*;
import utils.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/v1/*")
public class ApiServlet extends HttpServlet {

	private static final Logger log = LogManager.getLogger(ApiServlet.class);

	// Route definitions with HTTP method and handler
	private static final Map<String, Map<String, RouteHandler>> routes = new HashMap<>();

	static {
		// TEST BackendManager
		addRoute("GET", ApiConstants.TEST_URL, TestServlet::test);

		// BACKEND
		addRoute("GET", ApiConstants.BACKEND_GET_ALL_SECRETS, BackendManager::getAllSecrets);

		// PROJECTS
		addRoute("GET", ApiConstants.PROJECTS_LIST_ALL, ProjectsList::listAllProjects);
		addRoute("POST", ApiConstants.PROJECTS_CREATE, ProjectsCreate::createProject);
		addRoute("PATCH", ApiConstants.PROJECTS_UPDATE, ProjectsUpdate::updateProject);
		addRoute("GET", ApiConstants.PROJECTS_SUMMARY, ProjectsSummary::getProjectSummary);

		// ENVIRONMENTS
		addRoute("GET", ApiConstants.ENVS_LIST, EnvList::listAllEnv);
		addRoute("POST", ApiConstants.ENVS_CREATE, EnvCreate::createEnv);
		addRoute("PATCH", ApiConstants.ENVS_UPDATE, EnvUpdate::updateEnv);
		addRoute("DELETE", ApiConstants.ENVS_DELETE, EnvDelete::deleteEnv);

		// SECRETS
		addRoute("GET", ApiConstants.SECRETS_KEYS, SecretsList::listAllSecrets);
		addRoute("POST", ApiConstants.SECRETS_ADD, SecretsCreate::createSecret);
		addRoute("GET", ApiConstants.SECRETS_GET_VAL, SecretsGet::getSecret);
		addRoute("PUT", ApiConstants.SECRETS_UPDATE, SecretsUpdate::updateSecret);
		addRoute("DELETE", ApiConstants.SECRETS_DELETE, SecretsDelete::deleteSecret);

		// API KEYS
		addRoute("POST", ApiConstants.API_KEYS_CREATE, KeyCreate::createKey);
		addRoute("GET", ApiConstants.API_KEYS_LIST, KeyList::listAllKeys);
		addRoute("DELETE", ApiConstants.API_KEYS_REVOKE, KeyDelete::deleteKey);

		// HEALTH
		addRoute("GET", ApiConstants.HEALTH_CHECK, (req, resp, params) -> {
			HttpUtil.sendSimpleJson(resp, HttpServletResponse.SC_OK, "success",
					"Secrets app is up and running.");
			log.info("Health Check Accessed");
		});
	}

	private static void addRoute(String method, String path, RouteHandler handler) {
		routes.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String method = req.getMethod();
		// trim the ending slash for consistency
		String untrimmedPath = req.getRequestURI();
		String path = untrimmedPath.charAt(untrimmedPath.length() - 1) == '/' && untrimmedPath.length() > 1
				? untrimmedPath.substring(0, untrimmedPath.length() - 1)
				: untrimmedPath;

		// Remove context path if present
		String contextPath = req.getContextPath();
		if (path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		Map<String, RouteHandler> methodRoutes = routes.get(method);
		if (methodRoutes == null) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		// Try exact match first
		RouteHandler handler = methodRoutes.get(path);
		if (handler != null) {
			handler.handle(req, resp, new HashMap<>());
			return;
		}

		// Try pattern matching for parameterized routes
		for (Map.Entry<String, RouteHandler> entry : methodRoutes.entrySet()) {
			String routePattern = entry.getKey();
			if (routePattern.contains("{")) {
				Map<String, String> params = matchRoute(routePattern, path);
				if (params != null) {
					entry.getValue().handle(req, resp, params);
					return;
				}
			}
		}

		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private Map<String, String> matchRoute(String routePattern, String actualPath) {
		// Convert route pattern to regex
		String regex = routePattern.replaceAll("\\{([^}]+)}", "([^/]+)");
		Pattern pattern = Pattern.compile("^" + regex + "$");
		Matcher matcher = pattern.matcher(actualPath);

		if (!matcher.matches()) {
			return null;
		}

		Map<String, String> params = new HashMap<>();
		Pattern paramPattern = Pattern.compile("\\{([^}]+)}");
		Matcher paramMatcher = paramPattern.matcher(routePattern);

		int groupIndex = 1;
		while (paramMatcher.find()) {
			String paramName = paramMatcher.group(1);
			String paramValue = matcher.group(groupIndex++);
			params.put(paramName, paramValue);
		}

		return params;
	}

	@FunctionalInterface
	private interface RouteHandler {
		void handle(HttpServletRequest req, HttpServletResponse resp, Map<String, String> pathParams)
				throws IOException, ServletException;
	}
}
