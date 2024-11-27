package com.hackathon.finservice.Controllers;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackathon.finservice.Controllers.UserController.LoginRequest;
import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Services.JwtService;
import com.hackathon.finservice.Services.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @MockBean
  private JwtService jwtService;

  @Test
  void testRegisterUser_SuccessfulRegistration() throws Exception {
    // Arrange
    UserController.RegisterUser registerUser = new UserController.RegisterUser("Nuwe Test", "nuwe@nuwe.com",
        "NuweTest1$");

    when(userService.findByEmail(registerUser.email())).thenReturn(Optional.empty());

    String hashedPassword = "$2a$10$vYWBxACqEIPeoT0O5b0faOHp4ITAHSBvoHDzBePW7tPqzpvqKLi6G";

    User savedUser = new User(
        registerUser.name(),
        registerUser.email(),
        registerUser.password(),
        hashedPassword,
        List.of(new Account("19b332", 0.0d, AccountType.MAIN, 0))
    );
    when(userService.registerUser(registerUser.name(), registerUser.email(), registerUser.password())).thenReturn(
        savedUser);

    // Act & Assert
    mockMvc.perform(post("/users/register")
            .contentType("application/json")
            .content("{\"name\":\"Nuwe Test\",\"email\":\"nuwe@nuwe.com\",\"password\":\"NuweTest1$\"}"))
        .andExpect(status().isOk())
        .andExpect(content().json(
            "{\"name\":\"Nuwe Test\",\"email\":\"nuwe@nuwe.com\",\"accountNumber\":\"19b332\",\"accountType\":\"Main\",\"hashedPassword\":\"$2a$10$vYWBxACqEIPeoT0O5b0faOHp4ITAHSBvoHDzBePW7tPqzpvqKLi6G\"}"));

    verify(userService, times(1)).findByEmail(registerUser.email());
    verify(userService, times(1)).registerUser(registerUser.name(), registerUser.email(), registerUser.password());
  }

  @Test
  void loginUser_SuccessfulLogin() throws Exception {
    LoginRequest loginRequest = new LoginRequest("nuwe@nuwe.com", "NuweTest1$");
    User user = new User("Nuwe Test", loginRequest.identifier(), loginRequest.password(),
        "$2a$10$vYWBxACqEIPeoT0O5b0faOHp4ITAHSBvoHDzBePW7tPqzpvqKLi6G", emptyList());

    when(userService.findByEmail(loginRequest.identifier())).thenReturn(Optional.of(user));
    when(bCryptPasswordEncoder.matches(loginRequest.password(), user.hashedPassword())).thenReturn(true);
    when(jwtService.generateToken(user.email())).thenReturn("mocked-jwt-token");

    mockMvc.perform(post("/users/login")
            .contentType("application/json")
            .content("{\"identifier\":\"nuwe@nuwe.com\",\"password\":\"NuweTest1$\"}"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"token\":\"mocked-jwt-token\"}"));

    verify(userService, times(1)).findByEmail(loginRequest.identifier());
    verify(bCryptPasswordEncoder, times(1)).matches(loginRequest.password(), user.hashedPassword());
    verify(jwtService, times(1)).generateToken(user.email());
  }

  @Test
  void loginUser_BadCredentials() throws Exception {
    LoginRequest loginRequest = new LoginRequest("nuwe@nuwe.com", "wrongPassword");
    User user = new User("Nuwe Test", loginRequest.identifier(), "NuweTest1$",
        "$2a$10$vYWBxACqEIPeoT0O5b0faOHp4ITAHSBvoHDzBePW7tPqzpvqKLi6G", emptyList());

    when(userService.findByEmail(loginRequest.identifier())).thenReturn(Optional.of(user));
    when(bCryptPasswordEncoder.matches(loginRequest.password(), user.hashedPassword())).thenReturn(false);

    mockMvc.perform(post("/users/login")
            .contentType("application/json")
            .content("{\"identifier\":\"nuwe@nuwe.com\",\"password\":\"wrongPassword\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Bad credentials"));

    verify(userService, times(1)).findByEmail(loginRequest.identifier());
    verify(bCryptPasswordEncoder, times(1)).matches(loginRequest.password(), user.hashedPassword());
  }

  @Test
  void loginUser_UserNotFound() throws Exception {
    LoginRequest loginRequest = new LoginRequest("unknown@nuwe.com", "NuweTest1$");

    when(userService.findByEmail(loginRequest.identifier())).thenReturn(Optional.empty());

    mockMvc.perform(post("/users/login")
            .contentType("application/json")
            .content("{\"identifier\":\"unknown@nuwe.com\",\"password\":\"NuweTest1$\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("User not found for the given identifier: unknown@nuwe.com"));

    verify(userService, times(1)).findByEmail(loginRequest.identifier());
  }
}
