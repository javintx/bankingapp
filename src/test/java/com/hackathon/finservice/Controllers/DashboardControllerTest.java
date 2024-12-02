package com.hackathon.finservice.Controllers;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Util.JwtUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DashboardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class DashboardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JwtUtil jwtService;

  @Test
  void getUserInfo_validToken_returnsUserInfo() throws Exception {
    var token = "validToken";
    var user = new User("Nuwe Test", "nuwe@nuwe.com", "",
        "$2a$10$VNEntB38mHY.dJ9iDkgrjud2EZ/pWCC9IisqyKqL3cLjEM0L0zSZS", emptyList());
    var account = new Account("e62fa2", 0.0d, AccountType.MAIN, 0);
    user = new User(user.name(), user.email(), user.password(), user.hashedPassword(), List.of(account));
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.of(user));

    mockMvc.perform(get("/api/dashboard/user")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().json(
            "{\"name\":\"Nuwe Test\",\"email\":\"nuwe@nuwe.com\",\"accountNumber\":\"e62fa2\",\"accountType\":\"Main\",\"hashedPassword\":\"$2a$10$VNEntB38mHY.dJ9iDkgrjud2EZ/pWCC9IisqyKqL3cLjEM0L0zSZS\"}"));
  }

  @Test
  void getUserInfo_invalidToken_returnsAccessDenied() throws Exception {
    var token = "invalidToken";
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/dashboard/user")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Access Denied"));
  }

  @Test
  void getAccountInfo_validToken_returnsAccountInfo() throws Exception {
    var token = "validToken";
    var user = new User("Nuwe Test", "nuwe@nuwe.com", "",
        "$2a$10$VNEntB38mHY.dJ9iDkgrjud2EZ/pWCC9IisqyKqL3cLjEM0L0zSZS", emptyList());
    var account = new Account("e62fa2", 0.0d, AccountType.MAIN, 0);
    user = new User(user.name(), user.email(), user.password(), user.hashedPassword(), List.of(account));
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.of(user));

    mockMvc.perform(get("/api/dashboard/account")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().json(
            "{\"accountNumber\":\"e62fa2\",\"balance\":0.0,\"accountType\":\"Main\"}"));
  }

  @Test
  void getAccountInfo_invalidToken_returnsAccessDenied() throws Exception {
    var token = "invalidToken";
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/dashboard/account")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Access Denied"));
  }

  @Test
  void getSpecificAccountInfo_validToken_validIndex_returnsAccountInfo() throws Exception {
    var token = "validToken";
    var user = new User("Nuwe Test", "nuwe@nuwe.com", "",
        "$2a$10$VNEntB38mHY.dJ9iDkgrjud2EZ/pWCC9IisqyKqL3cLjEM0L0zSZS", emptyList());
    var account = new Account("e62fa2", 0.0d, AccountType.MAIN, 0);
    user = new User(user.name(), user.email(), user.password(), user.hashedPassword(), List.of(account));
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.of(user));

    mockMvc.perform(get("/api/dashboard/account/0")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().json(
            "{\"accountNumber\":\"e62fa2\",\"balance\":0.0,\"accountType\":\"Main\"}"));
  }

  @Test
  void getSpecificAccountInfo_validToken_invalidIndex_returnsNotFound() throws Exception {
    var token = "validToken";
    var user = new User("Nuwe Test", "nuwe@nuwe.com", "",
        "$2a$10$VNEntB38mHY.dJ9iDkgrjud2EZ/pWCC9IisqyKqL3cLjEM0L0zSZS", emptyList());
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.of(user));

    mockMvc.perform(get("/api/dashboard/account/1")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Account not found"));
  }

  @Test
  void getSpecificAccountInfo_invalidToken_returnsAccessDenied() throws Exception {
    var token = "invalidToken";
    when(jwtService.getValidUserFromToken("Bearer " + token)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/dashboard/account/0")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Access Denied"));
  }

}