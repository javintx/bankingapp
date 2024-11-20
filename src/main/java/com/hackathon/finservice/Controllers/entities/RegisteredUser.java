package com.hackathon.finservice.Controllers.entities;

import com.hackathon.finservice.Entities.User;

public record RegisteredUser(String name, String email, String accountNumber, String accountType,
                             String hashedPassword) {

  public static RegisteredUser fromUser(User user) {
    return new RegisteredUser(user.name(), user.email(), user.accountNumber().toString(), user.accountType(),
        user.hashedPassword());
  }

}
