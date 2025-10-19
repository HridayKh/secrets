package Secrets;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestServlet {

	public void test(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params) {
		System.out.println("This is a test servlet.");
	}

}
