package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Services.UserService;
import com.hackathon.finservice.Util.JsonUtil;
import com.hackathon.finservice.Util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  private final JwtUtil jwtUtil;

  @Autowired
  public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil) {
    this.userService = userService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping(value = "/register")
  public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUser registerUser) {
    return userService.findByEmail(registerUser.email())
        .map(user -> ResponseEntity.badRequest().body("Email already exists"))
        .orElseGet(() -> {
          User savedUser = userService.save(
              registerUser.name(),
              registerUser.email(),
              registerUser.password()
          );
          return ResponseEntity.ok(JsonUtil.toJson(new RegisteredUser(
              savedUser.name(),
              savedUser.email(),
              savedUser.accounts().getFirst().accountNumber(),
              savedUser.accounts().getFirst().accountType().type(),
              savedUser.hashedPassword()
          )));
        });
  }

  @PostMapping(value = "/login")
  public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
    return userService.findByEmail(loginRequest.identifier())
        .map(user -> {
          if (bCryptPasswordEncoder.matches(loginRequest.password(), user.hashedPassword())) {
            return ResponseEntity.ok(JsonUtil.toJson(new LoginResponse(jwtUtil.generateToken(user.email()))));
          } else {
            return ResponseEntity.status(401).body("Bad credentials");
          }
        })
        .orElseGet(() -> ResponseEntity.badRequest()
            .body("User not found for the given identifier: " + loginRequest.identifier()));
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
    invalidateJwtToken(token);
    return ResponseEntity.ok("Logged out successfully");
  }

  private void invalidateJwtToken(String token) {
    // Implementación para invalidar el token JWT
  }

  public record RegisterUser(@NotEmpty String name, @Email @NotEmpty String email, @NotEmpty String password) {

  }

  public record RegisteredUser(String name, String email, String accountNumber, String accountType,
                               String hashedPassword) {

  }

  public record LoginRequest(String identifier, String password) {

  }

  public record LoginResponse(String token) {

  }


}
