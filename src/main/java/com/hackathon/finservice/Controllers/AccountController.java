package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Services.AccountService;
import com.hackathon.finservice.Services.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

  private final JwtService jwtService;
  private final AccountService accountService;

  @Autowired
  public AccountController(JwtService jwtService, AccountService accountService) {
    this.jwtService = jwtService;
    this.accountService = accountService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createAccount(@RequestBody @Valid CreateAccountRequest createAccountRequest,
      @RequestHeader("Authorization") String token) {
    return jwtService.getValidUserFromToken(token)
        .map(user -> {
          accountService.createAccount(createAccountRequest.accountNumber(), createAccountRequest.accountType(), user);
          return ResponseEntity.ok("New account added successfully for user");
        })
        .orElseGet(() -> ResponseEntity.status(400).body("Access Denied"));
  }

  public record CreateAccountRequest(@NotEmpty String accountNumber, @Valid AccountType accountType) {

  }

}
