package secrets;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter("/v1/*")
public class AuthFIlter implements Filter {

	private static final Logger log = LogManager.getLogger(AuthFIlter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Auth Filter Created.");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		log.info("Auth Filter Destroyed.");
	}
}
