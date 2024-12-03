package com.hackathon.finservice.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final List<String> invalidatedTokens = new CopyOnWriteArrayList<>();

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expiration}")
  private long expiration;
  @Value("${jwt.header}")
  private String header;
  @Value("${jwt.prefix}")
  private String prefix;

  public String generateToken(String email) {
    return Jwts
        .builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public void invalidateToken(String token) {
    invalidatedTokens.add(token.startsWith(prefix) ? token.substring(prefix.length()).trim() : token);
  }

  public boolean isTokenInvalid(String token) {
    return invalidatedTokens.contains(token);
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(header);
    if (bearerToken != null && bearerToken.startsWith(prefix)) {
      return bearerToken.substring(prefix.length()).trim();
    }
    return null;
  }

  public Claims resolveClaims(HttpServletRequest req) {
    try {
      String token = resolveToken(req);
      if (token != null) {
        return parseJwtClaims(token);
      }
      return null;
    } catch (ExpiredJwtException ex) {
      req.setAttribute("expired", ex.getMessage());
      throw ex;
    } catch (Exception ex) {
      req.setAttribute("invalid", ex.getMessage());
      throw ex;
    }
  }

  private Claims parseJwtClaims(String token) {
    return Jwts
        .parser()
        .setSigningKey(secret)
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean validateClaims(Claims claims) throws AuthenticationException {
    try {
      return claims.getExpiration().after(new Date());
    } catch (Exception e) {
      throw e;
    }
  }
}