package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Services.JwtService;
import com.hackathon.finservice.Services.UserService;
import com.hackathon.finservice.Util.JsonUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {

  private final UserService userService;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  private final JwtService jwtService;

  @Autowired
  public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService) {
    this.userService = userService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.jwtService = jwtService;
  }

  @PostMapping(value = "/register")
  public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUser registerUser) {
    return userService.findByEmail(registerUser.email())
        .map(user -> ResponseEntity.badRequest().body("Email already exists"))
        .orElseGet(() -> {
          var savedUser = userService.registerUser(
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
            return ResponseEntity.ok(JsonUtil.toJson(new LoginResponse(jwtService.generateToken(user.email()))));
          } else {
            return ResponseEntity.status(401).body("Bad credentials");
          }
        })
        .orElseGet(() -> ResponseEntity.badRequest()
            .body("User not found for the given identifier: " + loginRequest.identifier()));
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
    jwtService.invalidateToken(token);
    return ResponseEntity.ok("Logged out successfully");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleBadRequestExceptions(MethodArgumentNotValidException ex) {
    return ResponseEntity
        .badRequest()
        .body(
            ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "))
        );
  }

  public record RegisterUser(
      @NotEmpty
      String name,

      @Email(message = "Invalid email: ${validatedValue}")
      @NotEmpty
      String email,

      @NotEmpty
      @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
      @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
      @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must contain at least one special character")
      @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespace")
      @Size(min = 8, message = "Password must be at least 8 characters long")
      @Size(max = 128, message = "Password must be less than 128 characters long")
      String password) {

  }

  public record RegisteredUser(String name, String email, String accountNumber, String accountType,
                               String hashedPassword) {

  }

  public record LoginRequest(String identifier, String password) {

  }

  public record LoginResponse(String token) {

  }


}
