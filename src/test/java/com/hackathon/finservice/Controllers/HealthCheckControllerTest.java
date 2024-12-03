package com.hackathon.finservice.Controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackathon.finservice.Util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HealthCheckController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class HealthCheckControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JwtUtil jwtUtil;
  
  @Test
  public void healthCheck_ReturnsApiIsWorking() throws Exception {
    mockMvc.perform(get("/health"))
        .andExpect(status().isOk())
        .andExpect(content().string("API is working"));
  }
}