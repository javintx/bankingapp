package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;


  @Autowired
  public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  public void createAccount(String accountNumber, AccountType accountType, User user) {
    var accountCreated = accountRepository.save(new Account(
        accountNumber,
        0.0d,
        accountType,
        user.accounts().size()
    ));

    user.accounts().add(accountCreated);
    userRepository.save(user);
  }
}
