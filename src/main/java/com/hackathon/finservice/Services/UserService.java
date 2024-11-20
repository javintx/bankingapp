package com.hackathon.finservice.Services;

import static com.hackathon.finservice.Util.PasswordUtil.hashPassword;

import com.hackathon.finservice.Controllers.entities.RegisterUser;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Services.exceptions.UserException;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User registerUser(@Valid RegisterUser user) throws UserException {
    Optional<User> existingUser = userRepository.findByEmail(user.email());
    if (existingUser.isPresent()) {
      throw new UserException("Email already exists");
    }

    User newUser = new User(user.name(), user.email(), user.password(), UUID.randomUUID(), "Main",
        hashPassword(user.password()));

    return userRepository.save(newUser);
  }
}