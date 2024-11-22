package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterUser registerUser) {
    if (userRepository.findByEmail(registerUser.email()) != null) {
      return ResponseEntity.badRequest().body("Email already exists");
    }
    userRepository.save(
        new User(
            registerUser.name(),
            registerUser.email(),
            registerUser.password(),
            UUID.randomUUID(),
            "Main",
            bCryptPasswordEncoder.encode(registerUser.password())
        )
    );

    return ResponseEntity.ok(registerUser);
  }

  public record RegisterUser(@NotEmpty String name, @Email @NotEmpty String email, @NotEmpty String password) {

  }

}
