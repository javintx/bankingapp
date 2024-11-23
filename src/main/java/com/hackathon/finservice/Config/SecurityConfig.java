package com.hackathon.finservice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf
        .ignoringRequestMatchers("/health", "/api/users/register")
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

    http.headers(headers -> headers
        .contentSecurityPolicy(contentSecurityPolicyConfig -> contentSecurityPolicyConfig.policyDirectives(
            "default-src 'self'; script-src 'self' 'unsafe-inline'; object-src 'none';")));

    http.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));

    http.cors(Customizer.withDefaults())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.GET, "/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
            .anyRequest().authenticated()
        );

    return http.build();

//    http
//        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
//        .csrf(AbstractHttpConfigurer::disable)
//        .cors(Customizer.withDefaults())
//        .authorizeHttpRequests(authorize -> authorize
//            .requestMatchers(HttpMethod.GET, "/health").permitAll()
//            .requestMatchers("/api/users/register").permitAll()
//            .anyRequest().authenticated()
//        );
//    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
