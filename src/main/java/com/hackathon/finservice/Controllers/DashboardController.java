package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Services.JwtService;
import com.hackathon.finservice.Services.UserService;
import com.hackathon.finservice.Util.JsonUtil;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final UserService userService;
  private final JwtService jwtService;

  @Autowired
  public DashboardController(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
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
        ).orElseGet(() -> ResponseEntity.noContent().build());
  }

  @GetMapping("/account")
  public ResponseEntity<?> getAccountInfo(
      @RequestHeader("Authorization")
      String token) {
    return jwtService.getValidUserFromToken(token)
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
  public ResponseEntity<?> getSpecificAccountInfo(
      @PathVariable int index,

      @RequestHeader("Authorization")
      String token
  ) {
    return jwtService.getValidUserFromToken(token)
        .map(user -> {
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
        }).orElseGet(() -> ResponseEntity.status(401).body("Access Denied"));
  }

  public record UserInfo(String name, String email, String accountNumber, String accountType,
                         String hashedPassword) {

  }

  public record AccountInfo(String accountNumber, double balance, String accountType) {

  }

}
