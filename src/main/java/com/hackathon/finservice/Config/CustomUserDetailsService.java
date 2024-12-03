package com.hackathon.finservice.Config;

import com.hackathon.finservice.Repositories.UserRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found for the given identifier: " + email));
    return new User(
        user.email(),
        user.hashedPassword(),
        new ArrayList<>());
  }
}
