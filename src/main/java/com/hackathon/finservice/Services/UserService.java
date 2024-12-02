package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, AccountRepository accountRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public User getUserByEmail(String email) {
    return userRepository.getUserByEmail(email);
  }

  @Transactional
  public User registerUser(String name, String email, String password) {
    var savedAccount = accountRepository.save(new Account(UUID.randomUUID().toString(), 0.0d, AccountType.MAIN, 0));

    var user = new User(name, email, password, passwordEncoder.encode(password), List.of(savedAccount));

    return userRepository.save(user);
  }
}
