package by.hackaton.bookcrossing.util;

import by.hackaton.bookcrossing.configuration.oauth.CustomOAuth2User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@Slf4j
public class AuthUtils {

    public static String getEmailFromAuth(Authentication auth) {
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            log.info("User authentication: " + auth.getPrincipal().toString());
            return auth.getPrincipal().toString();
        }
        log.info("User authentication: " + ((CustomOAuth2User) auth.getPrincipal()).getEmail());
        return ((CustomOAuth2User) auth.getPrincipal()).getEmail();
    }
}
