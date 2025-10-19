package Secrets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestServlet {

	public void test(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("This is a test servlet.");
	}

}
