package secrets;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class TestServlet {
	private static final Logger log = LogManager.getLogger(TestServlet.class);

	public static void test(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params) {
		log.info(System.getenv("VITE_SECRETS_FRONTEND"));
		log.info(System.getenv("SECRET"));
		log.info(System.getenv("ADMIN_API_KEYS"));
        log.info("info:\n\treq:{}\n\t{}\n\t{}", req.toString(), resp.toString(), params.toString());
	}

}
