package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Services.UserService;
import com.hackathon.finservice.Util.JsonUtil;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<?> getUserInfo(Principal principal) {
    return userService.findByEmail(principal.getName())
        .map(user -> ResponseEntity.ok(
                JsonUtil.toJson(
                    new UserInfo(
                        user.name(),
                        user.email(),
                        user.accounts().getFirst().accountNumber(),
                        user.accounts().getFirst().accountType().type(),
                        user.hashedPassword()
                    )
                )
            )
        ).orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  @GetMapping("/account")
  public ResponseEntity<?> getAccountInfo(Principal principal) {
    return userService.findByEmail(principal.getName())
        .map(user -> ResponseEntity.ok(
                JsonUtil.toJson(
                    new AccountInfo(
                        user.accounts().getFirst().accountNumber(),
                        user.accounts().getFirst().balance(),
                        user.accounts().getFirst().accountType().type()
                    )
                )
            )
        ).orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  @GetMapping("/account/{index}")
  public ResponseEntity<?> getSpecificAccountInfo(@PathVariable int index, Principal principal) {
    return userService.findByEmail(principal.getName())
        .map(user -> (index < 0 || index >= user.accounts().size())
            ? ResponseEntity.status(404).body("Account not found")
            : ResponseEntity.ok(JsonUtil.toJson(
                new AccountInfo(
                    user.accounts().get(index).accountNumber(),
                    user.accounts().get(index).balance(),
                    user.accounts().get(index).accountType().type()
                )
            ))).orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  public record UserInfo(String name, String email, String accountNumber, String accountType,
                         String hashedPassword) {

  }

  public record AccountInfo(String accountNumber, double balance, String accountType) {

  }

}
