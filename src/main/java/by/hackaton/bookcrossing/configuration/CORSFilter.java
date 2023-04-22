package by.hackaton.bookcrossing.configuration;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class CORSFilter implements Filter {
    private static final String FRONT_URL = "http://localhost:3000"; // URL
    private static final String BACK_URL = "http://localhost:8181"; // URL
    private static final String MAIN_URL = "https://staronki.herokuapp.com"; // OTHER URL
    private static final String HTTPS_STARONKI_URL = "https://www.staronki.space"; // OTHER URL
    private static final String HTTP_STARONKI_URL = "http://www.staronki.space"; // OTHER URL

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) res;
        final HttpServletRequest request = (HttpServletRequest) req;
        if (FRONT_URL.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", FRONT_URL);
        } else if (MAIN_URL.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", MAIN_URL);
        } else if (BACK_URL.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", BACK_URL);
        } else if (HTTPS_STARONKI_URL.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", HTTPS_STARONKI_URL);
        } else if (HTTP_STARONKI_URL.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", HTTP_STARONKI_URL);
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin, Access-Control-Allow-Headers, Origin, Authorization, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(req, res);
    }
}
