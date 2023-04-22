package by.hackaton.bookcrossing.configuration;

import by.hackaton.bookcrossing.configuration.oauth.CustomOAuth2User;
import by.hackaton.bookcrossing.configuration.oauth.JwtTokenFilter;
import by.hackaton.bookcrossing.dto.security.CustomAuthenticationProvider;
import by.hackaton.bookcrossing.dto.security.TokenProvider;
import by.hackaton.bookcrossing.service.CustomOAuth2UserService;
import by.hackaton.bookcrossing.service.UserDetailsServiceImpl;
import by.hackaton.bookcrossing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Autowired
    private UserService userService;
    @Autowired
    private CustomAuthenticationProvider authProvider;
    @Autowired
    private TokenProvider tokenProvider;
    private CORSFilter corsFilter;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void setCustomUserDetailsService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setCorsFilter(CORSFilter corsFilter) {
        this.corsFilter = corsFilter;
    }

    private static final String[] SWAGGER_ENDPOINTS = new String[]{
            "/v2/api-docs",
            "/swagger-resources/configuration/ui",
            "/swagger-resources",
            "/swagger-resources/configuration/security",
            "/swagger-ui/index.html",
            "/swagger-ui",
            "/webjars/**",
            "/swagger-resources/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/swagger-resources/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtTokenFilter customFilter = new JwtTokenFilter(tokenProvider);
        http
                .cors()
                .disable()
                .addFilterBefore(corsFilter, LogoutFilter.class)
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/auth/*").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/subscribers").permitAll()
                .antMatchers("/books/*").permitAll()
                .antMatchers(HttpMethod.POST, "/books").authenticated()
                .antMatchers("/books").permitAll()
                .antMatchers(HttpMethod.GET,"/organizations").permitAll()
                .antMatchers(SWAGGER_ENDPOINTS).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .and().csrf().disable()
                .oauth2Login()
                .loginPage("https://www.staronki.space/registration")
                .userInfoEndpoint()
                .userService(oauthUserService)
                .and()
                .successHandler((request, response, authentication) -> {
                    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                    userService.processOAuthPostLogin(oauthUser.getEmail());
                    response.sendRedirect("https://www.staronki.space/");
                });
        return http.build();
    }
}
