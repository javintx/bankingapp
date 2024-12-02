package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Services.AccountService;
import com.hackathon.finservice.Services.UserService;
import com.hackathon.finservice.Util.JsonUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

  private final AccountService accountService;
  private final UserService userService;

  @Autowired
  public AccountController(AccountService accountService, UserService userService) {
    this.accountService = accountService;
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createAccount(@RequestBody @Valid CreateAccountRequest createAccountRequest) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    accountService.createAccount(createAccountRequest.accountNumber(), createAccountRequest.accountType(), user);
    return ResponseEntity.ok("New account added successfully for user");
  }

  @PostMapping("/deposit")
  public ResponseEntity<String> deposit(@RequestBody @Valid DepositRequest depositRequest) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    accountService.deposit(depositRequest.amount(), user);
    return ResponseEntity.ok(JsonUtil.toJson(new AccountResponse("Cash deposited successfully")));
  }

  @PostMapping("/withdraw")
  public ResponseEntity<String> withdraw(@RequestBody @Valid WithdrawRequest withdrawRequest) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    if (accountService.withdraw(withdrawRequest.amount(), user)) {
      return ResponseEntity.ok(JsonUtil.toJson(new AccountResponse("Cash withdrawn successfully")));
    } else {
      return ResponseEntity.ok().body(JsonUtil.toJson(new AccountResponse("Insufficient balance")));
    }
  }

  @PostMapping("/fund-transfer")
  public ResponseEntity<String> fundTransfer(@RequestBody @Valid FundTransferRequest fundTransferRequest) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    if (accountService.fundTransfer(fundTransferRequest.amount(), fundTransferRequest.targetAccountNumber(), user)) {
      return ResponseEntity.ok(JsonUtil.toJson(new AccountResponse("Fund transferred successfully")));
    } else {
      return ResponseEntity.ok().body(JsonUtil.toJson(new AccountResponse("Insufficient balance")));
    }
  }

  @GetMapping("/transactions")
  public ResponseEntity<?> getTransactions() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    return ResponseEntity.ok(JsonUtil.toJson(user.accounts().getFirst().transactions()));
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
