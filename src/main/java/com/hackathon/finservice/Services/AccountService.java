package com.hackathon.finservice.Services;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.Transaction;
import com.hackathon.finservice.Entities.TransactionStatus;
import com.hackathon.finservice.Entities.TransactionType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.TransactionRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final TransactionRepository transactionRepository;


  @Autowired
  public AccountService(AccountRepository accountRepository, UserRepository userRepository,
      TransactionRepository transactionRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.transactionRepository = transactionRepository;
  }

  private static boolean insufficientFunds(double amount, Account mainAccount) {
    return mainAccount.balance() < amount;
  }

  @Transactional
  public void createAccount(String accountNumber, AccountType accountType, User user) {
    try {
      var account = accountRepository.save(
          new Account(
              accountNumber,
              0.0d,
              accountType,
              user.accounts().size()
          )
      );
      user.accounts().add(account);
      userRepository.save(user);
    } catch (DataIntegrityViolationException ex) {
      throw new EntityExistsException("Duplicate account number");
    }
  }

  @Transactional
  public void deposit(double amount, User user) {
    var mainAccount = user.accounts().getFirst();

    var transaction = transactionRepository.save(
        new Transaction(
            amount,
            TransactionType.CASH_DEPOSIT,
            TransactionStatus.PENDING,
            mainAccount.accountNumber()
        )
    );

    mainAccount.transactions().add(transaction);
    mainAccount.balance(mainAccount.balance() + applyDepositFee(amount));
    accountRepository.save(mainAccount);
  }

  private double applyDepositFee(double amount) {
    return amount > 50_000 ? amount * 0.98 : amount;
  }

  @Transactional
  public boolean withdraw(double amount, User user) {
    var mainAccount = user.accounts().getFirst();

    if (insufficientFunds(amount, mainAccount)) {
      return false;
    }

    var transaction = transactionRepository.save(
        new Transaction(
            amount,
            TransactionType.CASH_WITHDRAWAL,
            TransactionStatus.PENDING,
            mainAccount.accountNumber()
        )
    );

    mainAccount.balance(mainAccount.balance() - applyWithdrawFee(amount));
    mainAccount.transactions().add(transaction);
    accountRepository.save(mainAccount);

    return true;
  }

  private double applyWithdrawFee(double amount) {
    return amount > 10_000 ? amount * 1.01 : amount;
  }

  @Transactional
  public boolean fundTransfer(double amount, String targetAccountNumber, User user) {
    var mainAccount = user.accounts().getFirst();

    if (insufficientFunds(amount, mainAccount)) {
      return false;
    }

    if (isFraudulentAmount(amount)) {
      var transaction = transactionRepository.save(
          new Transaction(
              amount,
              TransactionType.CASH_TRANSFER,
              TransactionStatus.FRAUD,
              targetAccountNumber
          )
      );
      mainAccount.transactions().add(transaction);
    } else if (isFrequentTransfers(mainAccount, targetAccountNumber)) {
      var transaction = transactionRepository.save(
          new Transaction(
              amount,
              TransactionType.CASH_TRANSFER,
              TransactionStatus.FRAUD,
              targetAccountNumber
          )
      );
      mainAccount.transactions().add(transaction);
      markTransactionsAsFraud(mainAccount, targetAccountNumber);
    } else {
      var transaction = transactionRepository.save(
          new Transaction(
              amount,
              TransactionType.CASH_TRANSFER,
              TransactionStatus.PENDING,
              targetAccountNumber
          )
      );
      mainAccount.transactions().add(transaction);
      mainAccount.balance(mainAccount.balance() - amount);
    }

    accountRepository.save(mainAccount);
    return true;
  }

  private boolean isFraudulentAmount(double amount) {
    return amount > 80_000;
  }

  private boolean isFrequentTransfers(Account account, String targetAccountNumber) {
    Instant fiveSecondsAgo = Instant.now().minusSeconds(5);
    return account.transactions().stream()
        .filter(transaction -> transaction.transactionType() == TransactionType.CASH_TRANSFER)
        .filter(transferTransaction -> transferTransaction.targetAccountNumber().equals(targetAccountNumber))
        .filter(transferSameTargetAccountTransaction -> transferSameTargetAccountTransaction.transactionDate()
            .isAfter(fiveSecondsAgo))
        .count() > 4;
  }

  private void markTransactionsAsFraud(Account account, String targetAccountNumber) {
    Instant fiveSecondsAgo = account.transactions().getLast().transactionDate().minusSeconds(5);
    account.transactions().stream()
        .filter(transaction -> transaction.transactionType() == TransactionType.CASH_TRANSFER)
        .filter(transferTransaction -> transferTransaction.targetAccountNumber().equals(targetAccountNumber))
        .filter(transferSameTargetAccountTransaction -> transferSameTargetAccountTransaction.transactionDate()
            .isAfter(fiveSecondsAgo))
        .forEach(transaction -> transaction.transactionStatus(TransactionStatus.FRAUD));
  }
}
