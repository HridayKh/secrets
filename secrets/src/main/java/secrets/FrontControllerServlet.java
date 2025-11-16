package secrets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {
	private static final Logger log = LogManager.getLogger(FrontControllerServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getRequestURI().substring(req.getContextPath().length());
		if (path.contains(".")) {
			req.getServletContext().getNamedDispatcher("default").forward(req, resp);
			return;
		}
		if (path.startsWith("/v1/")) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.setContentType("text/plain");
			resp.getWriter().write("Not forwarded by FrontControllerServlet: " + path);
			return;
		}
		req.setAttribute("frontendPath", path);
		req.getRequestDispatcher("/index.html").forward(req, resp);
	}

}
