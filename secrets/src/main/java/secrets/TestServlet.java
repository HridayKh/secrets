package secrets;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestServlet {
	private static final Logger log = LogManager.getLogger(TestServlet.class);

	public void test(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params) {
		// This ERROR level message was working and confirmed Log4j2 setup.
		log.error("This is a test servlet.");

		// Replacing System.out.println with a logger call to ensure it gets routed to
		// the console.
		log.info("Test servlet handler executed successfully (yo).");
	}

}
