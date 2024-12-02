package com.hackathon.finservice.Controllers;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackathon.finservice.Controllers.AccountController.AccountResponse;
import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Services.AccountService;
import com.hackathon.finservice.Util.JwtUtil;
import com.hackathon.finservice.Util.JsonUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AccountControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JwtUtil jwtService;

  @MockBean
  private AccountService accountService;

  @Test
  void testCreateAccount() throws Exception {
    var token = "validToken";
    var user = new User("Nuwe Test", "nuwe@nuwe.com", "",
        "$2a$10$VNEntB38mHY.dJ9iDkgrjud2EZ/pWCC9IisqyKqL3cLjEM0L0zSZS", emptyList());
    var account = new Account("e62fa2", 0.0d, AccountType.MAIN, 0);
    user = new User(user.name(), user.email(), user.password(), user.hashedPassword(), List.of(account));
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.of(user));

    String requestBody = "{\"accountNumber\":\"12345\",\"accountType\":\"Main\"}";
    mockMvc.perform(post("/api/account/create")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().string("New account added successfully for user"));
  }

  @Test
  void testDeposit() throws Exception {
    String token = "valid-token";
    String requestBody = "{\"amount\":100.0}";

    when(jwtService.getValidUserFromToken(token)).thenReturn(Optional.of(new User()));
    mockMvc.perform(post("/api/account/deposit")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(JsonUtil.toJson(new AccountResponse("Cash deposited successfully"))));
  }

  @Test
  void testWithdraw() throws Exception {
    String token = "valid-token";
    String requestBody = "{\"amount\":50.0}";

    when(jwtService.getValidUserFromToken(token)).thenReturn(Optional.of(new User()));
    when(accountService.withdraw(50.0, new User())).thenReturn(true);
    mockMvc.perform(post("/api/account/withdraw")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(JsonUtil.toJson(new AccountResponse("Cash withdrawn successfully"))));
  }

  @Test
  void testFundTransfer() throws Exception {
    String token = "valid-token";
    String requestBody = "{\"amount\":100.0,\"targetAccountNumber\":\"67890\"}";

    when(jwtService.getValidUserFromToken(token)).thenReturn(Optional.of(new User()));
    when(accountService.fundTransfer(100.0, "67890", new User())).thenReturn(true);
    mockMvc.perform(post("/api/account/fund-transfer")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(JsonUtil.toJson(new AccountResponse("Fund transferred successfully"))));
  }

  @Test
  void testGetTransactions() throws Exception {
    String token = "valid-token";

    when(jwtService.getValidUserFromToken(token)).thenReturn(Optional.of(new User()));
    mockMvc.perform(get("/api/account/transactions")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(content().json(JsonUtil.toJson(new User().accounts().getFirst().transactions())));
  }
}