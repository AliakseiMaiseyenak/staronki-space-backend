package by.hackaton.bookcrossing.configuration.oauth;

import by.hackaton.bookcrossing.dto.security.TokenProvider;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {
    final TokenProvider tokenProvider;

    public JwtTokenFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        final String token = tokenProvider.resolveToken((HttpServletRequest) req);
        if (token != null && !token.isEmpty()) {
            final Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else if (SecurityContextHolder.getContext().getAuthentication() != null &&
                !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomOAuth2User)) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        filterChain.doFilter(req, res);
    }
}
