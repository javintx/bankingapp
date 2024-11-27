package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final UserService userService;
  private final List<String> invalidatedTokens = new CopyOnWriteArrayList<>();

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expiration}")
  private long expiration;
  @Value("${jwt.header}")
  private String header;

  @Autowired
  public JwtService(UserService userService) {
    this.userService = userService;
  }

  public String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Optional<User> getValidUserFromToken(String token) {
  return Optional.ofNullable(token)
      .map(rawToken -> rawToken.startsWith(header) ? rawToken.substring(header.length() + 1) : rawToken)
      .filter(notNullToken -> !invalidatedTokens.contains(notNullToken))
      .flatMap(validToken -> userService.findByEmail(extractEmail(validToken))
          .filter(user -> user.email().equals(extractEmail(validToken)) && !isTokenExpired(validToken)));
}

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    return claimsResolver
        .apply(Jwts
            .parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody()
        );
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public void invalidateToken(String token) {
    invalidatedTokens.add(token);
  }
}