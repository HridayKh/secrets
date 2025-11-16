package secrets;

import java.io.IOException;

import db.dbSecrets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CORSFilter implements Filter {

	private static final Logger log = LogManager.getLogger(CORSFilter.class);

	@Override
	public void init(FilterConfig filterConfig) {
		log.info("CORS Filter Created.");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;

		// https://api.HridayKh.in/secrets -> https://api.HridayKh.in
		String front_host = dbSecrets.FRONT_HOST.split("/")[0] + "//" + dbSecrets.FRONT_HOST.split("/")[2];
		// String front_host = "http://localhost:5173";

		res.setHeader("Access-Control-Allow-Origin", front_host);
		res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
		res.setHeader("Access-Control-Allow-Headers", "Content-Type");
		res.setHeader("Access-Control-Allow-Credentials", "true");
		res.setHeader("Access-Control-Max-Age", "3600");
		if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
			res.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		log.info("CORS Filter Destroyed.");
	}
}
