package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Services.AccountService;
import com.hackathon.finservice.Services.JwtService;
import com.hackathon.finservice.Util.JsonUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        .orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  @PostMapping("/deposit")
  public ResponseEntity<String> deposit(@RequestBody @Valid DepositRequest depositRequest,
      @RequestHeader("Authorization") String token) {
    return jwtService.getValidUserFromToken(token)
        .map(user -> {
          accountService.deposit(depositRequest.amount(), user);
          return ResponseEntity.ok(JsonUtil.toJson(new AccountResponse("Cash deposited successfully")));
        })
        .orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  @PostMapping("/withdraw")
  public ResponseEntity<String> withdraw(@RequestBody @Valid WithdrawRequest withdrawRequest,
      @RequestHeader("Authorization") String token) {
    return jwtService.getValidUserFromToken(token)
        .map(user -> {
          if (accountService.withdraw(withdrawRequest.amount(), user)) {
            return ResponseEntity.ok(JsonUtil.toJson(new AccountResponse("Cash withdrawn successfully")));
          } else {
            return ResponseEntity.ok().body(JsonUtil.toJson(new AccountResponse("Insufficient balance")));
          }
        })
        .orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  @PostMapping("/fund-transfer")
  public ResponseEntity<String> fundTransfer(@RequestBody @Valid FundTransferRequest fundTransferRequest,
      @RequestHeader("Authorization") String token) {
    return jwtService.getValidUserFromToken(token)
        .map(user -> {
          if (accountService.fundTransfer(fundTransferRequest.amount(), fundTransferRequest.targetAccountNumber(),
              user)) {
            return ResponseEntity.ok(JsonUtil.toJson(new AccountResponse("Fund transferred successfully")));
          } else {
            return ResponseEntity.ok().body(JsonUtil.toJson(new AccountResponse("Insufficient balance")));
          }
        })
        .orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  @GetMapping("/transactions")
  public ResponseEntity<?> getTransactions(@RequestHeader("Authorization") String token) {
    return jwtService.getValidUserFromToken(token)
        .map(user -> ResponseEntity.ok(JsonUtil.toJson(user.accounts().getFirst().transactions())))
        .orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  public record CreateAccountRequest(@NotEmpty String accountNumber, @Valid AccountType accountType) {

  }

  public record DepositRequest(@Positive double amount) {

  }

  public record AccountResponse(String msg) {

  }

  public record WithdrawRequest(@Positive double amount) {

  }

  public record FundTransferRequest(@Positive double amount, @NotEmpty String targetAccountNumber) {

  }
}
