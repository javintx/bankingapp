package com.hackathon.finservice.Config;

import com.hackathon.finservice.Util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Autowired
  public JwtAuthorizationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String accessToken = jwtUtil.resolveToken(request);
    if (accessToken == null) {
      chain.doFilter(request, response);
      return;
    }

    if(jwtUtil.isTokenInvalid(accessToken)) {
      chain.doFilter(request, response);
      return;
    }

    Claims claims = jwtUtil.resolveClaims(request);
    if (claims != null && jwtUtil.validateClaims(claims)) {
      SecurityContextHolder
          .getContext()
          .setAuthentication(
              new UsernamePasswordAuthenticationToken(claims.getSubject(), "", new ArrayList<>())
          );
    }

    chain.doFilter(request, response);
  }

}
