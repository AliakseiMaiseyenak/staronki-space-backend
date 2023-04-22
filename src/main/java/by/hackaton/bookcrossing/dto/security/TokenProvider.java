package by.hackaton.bookcrossing.dto.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    public static final String JWT_TOKEN_TYPE = "Bearer";
    public static final String AUTHORITIES_KEY = "AUTHORITIES";
    public static final String PASSWORD = "password";

    @Value("${security.jwt.token.issuer:bookcrossing}")
    String issuer;

    @Value("${security.jwt.token.secret-key:jWnZr4u7x!A%D*G-KaPdSgVkYp2s5v8y/B?E(H+MbQeThWmZq4t6w9z$C&F)J@Nc}")
    String secretKey;

    public String createToken(Authentication authentication, String password) {
        final String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        final Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put(AUTHORITIES_KEY, authorities);
        claims.put(PASSWORD, password);
        return Jwts.builder().setClaims(claims).signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256).setIssuer(issuer).compact();
    }

    public Authentication getAuthentication(String token) {
        return new UsernamePasswordAuthenticationToken(getSubject(token), getPassword(token), getRoles(token));
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public String getPassword(String token) {
        return getClaims(token).get(PASSWORD).toString();
    }

    public String getAudience(String token) {
        return getClaims(token).getAudience();
    }

    public String getId(String token) {
        return getClaims(token).getId();
    }

    public List<GrantedAuthority> getRoles(String token) {
        return Collections.emptyList();//Arrays.stream(getClaims(token).get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(JWT_TOKEN_TYPE)) {
            return bearerToken.substring(JWT_TOKEN_TYPE.length() + 1/* space */);
        }
        return null;
    }

    public String resolveUserAgent(HttpServletRequest req) {
        String userAgent = req.getHeader(HttpHeaders.USER_AGENT);
        return userAgent != null ? userAgent : "";
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token).getBody();
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
