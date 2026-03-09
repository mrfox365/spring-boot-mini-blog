package com.example.project.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filter that intercepts incoming HTTP requests to validate JWT tokens in the Authorization header.
 * If a valid token is found, it populates the Spring Security context with the user's authentication details.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;

  /**
   * Performs the actual filtering logic for each request.
   *
   * @param request     the incoming HTTP request
   * @param response    the outgoing HTTP response
   * @param filterChain the filter chain to execute
   * @throws ServletException if a servlet-related error occurs
   * @throws IOException      if an I/O error occurs during processing
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);
    username = jwtService.extractUsername(jwt);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      if (jwtService.isTokenValid(jwt, username)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            username, null, new ArrayList<>()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}