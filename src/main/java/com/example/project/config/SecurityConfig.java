package com.example.project.config;

import com.example.project.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security configuration class for the application.
 * Defines authentication, authorization rules, and CSRF protection mechanisms
 * compatible with Single Page Applications (SPA).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationFilter jwtAuthFilter;

  /**
   * Configures the password encoder bean used for secure password hashing.
   *
   * @return a BCryptPasswordEncoder instance with a strength factor of 12
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  /**
   * Configures the security filter chain for HTTP requests.
   * Sets up stateless session management, JWT filtering, and cookie-based CSRF protection.
   * Disables lazy CSRF token generation to ensure immediate token availability.
   *
   * @param http the HttpSecurity object to configure
   * @return the constructed SecurityFilterChain
   * @throws Exception if an error occurs during the security configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Configure request handler to disable lazy CSRF token generation
    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    requestHandler.setCsrfRequestAttributeName(null);

    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(requestHandler)
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/auth.html", "/reset-password.html").permitAll()
            .requestMatchers("/css/**", "/js/**", "/static/**", "/*.html").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/blog/posts", "/api/blog/comments/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class)
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
            .permitAll()
        );

    return http.build();
  }

  /**
   * Internal filter designed to force the resolution of the CSRF token.
   * This ensures that the XSRF-TOKEN cookie is populated in the response.
   */
  private static final class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
      CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
      if (csrfToken != null) {
        csrfToken.getToken(); // Forces cookie generation
      }
      filterChain.doFilter(request, response);
    }
  }
}