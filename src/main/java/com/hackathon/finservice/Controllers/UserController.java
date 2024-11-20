package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Controllers.entities.RegisterUser;
import com.hackathon.finservice.Controllers.entities.RegisteredUser;
import com.hackathon.finservice.Services.UserService;
import com.hackathon.finservice.Services.exceptions.UserException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUser registerUser) {
    try {
      return ResponseEntity.ok(RegisteredUser.fromUser(userService.registerUser(registerUser)));
    } catch (UserException e) {
      return ResponseEntity.status(400).body("Email already exists");
    }
  }

}
