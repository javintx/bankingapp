package com.hackathon.finservice.Services;

import static java.util.Collections.emptyList;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public User save(String name, String email, String password) {
    User savedUser = userRepository.save(
        new User(
            name,
            email,
            password,
            bCryptPasswordEncoder.encode(password),
            emptyList()
        )
    );
    savedUser = new User(
        savedUser.name(),
        savedUser.email(),
        savedUser.password(),
        savedUser.hashedPassword(),
        List.of(Account.createMainAccount(savedUser))
    );
    return userRepository.save(savedUser);
  }
}
