package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//  @PostMapping(value = "/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterUser registerUser) {
    return userRepository.findByEmail(registerUser.email())
        .map(user -> ResponseEntity.badRequest().body("Email already exists"))
        .orElseGet(() -> {
          User savedUser = userRepository.save(
              new User(
                  registerUser.name(),
                  registerUser.email(),
                  registerUser.password(),
                  UUID.randomUUID().toString(),
                  "Main",
                  bCryptPasswordEncoder.encode(registerUser.password())
              )
          );
          return ResponseEntity.ok(new RegisteredUser(
              savedUser.name(),
              savedUser.email(),
              savedUser.accountNumber(),
              savedUser.accountType(),
              savedUser.hashedPassword()
          ).toString());
        });
  }

  public record RegisterUser(@NotEmpty String name, @Email @NotEmpty String email, @NotEmpty String password) {

  }

  public record RegisteredUser(String name, String email, String accountNumber, String accountType,
                               String hashedPassword) {

  }

}
