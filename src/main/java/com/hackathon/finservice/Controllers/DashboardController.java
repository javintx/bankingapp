package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Services.UserService;
import com.hackathon.finservice.Util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final UserService userService;

  @Autowired
  public DashboardController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user")
  public ResponseEntity<?> getUserInfo() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    return ResponseEntity.ok(
        JsonUtil.toJson(
            new UserInfo(
                user.name(),
                user.email(),
                user.accounts().getFirst().accountNumber(),
                user.accounts().getFirst().accountType().type(),
                user.hashedPassword()
            )
        )
    );
  }

  @GetMapping("/account")
  public ResponseEntity<?> getAccountInfo() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    return ResponseEntity.ok(
        JsonUtil.toJson(
            new AccountInfo(
                user.accounts().getFirst().accountNumber(),
                user.accounts().getFirst().balance(),
                user.accounts().getFirst().accountType().type()
            )
        )
    );
  }

  @GetMapping("/account/{index}")
  public ResponseEntity<?> getSpecificAccountInfo(@PathVariable int index) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userService.getUserByEmail(email);
    if (index < 0 || index >= user.accounts().size()) {
      return ResponseEntity.status(404).body("Account not found");
    }
    var account = user.accounts().get(index);
    return ResponseEntity.ok(JsonUtil.toJson(
        new AccountInfo(
            account.accountNumber(),
            account.balance(),
            account.accountType().type()
        )
    ));
  }

  public record UserInfo(String name, String email, String accountNumber, String accountType,
                         String hashedPassword) {

  }

  public record AccountInfo(String accountNumber, double balance, String accountType) {

  }

}
