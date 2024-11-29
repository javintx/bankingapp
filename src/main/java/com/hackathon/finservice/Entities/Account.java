package com.hackathon.finservice.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class Account {

  @Id
  @Column(nullable = false)
  private String accountNumber;

  @Column(nullable = false)
  private double balance;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AccountType accountType;

  @Column(nullable = false)
  private int creationOrder;

  @OneToMany
  @OrderBy("id DESC")
  private List<Transaction> transactions;

  public Account(String accountNumber, double balance, AccountType accountType, int creationOrder) {
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.accountType = accountType;
    this.creationOrder = creationOrder;
    this.transactions = new ArrayList<>();
  }

}