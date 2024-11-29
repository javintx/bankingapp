package com.hackathon.finservice.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class Transaction {

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @Column(nullable = false)
  private double amount;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TransactionStatus transactionStatus;

  @Column(nullable = false)
  private Instant transactionDate;

  @Column(nullable = false)
  private String sourceAccountNumber;

  @Column(nullable = false)
  private String targetAccountNumber;

  public Transaction(
      double amount,
      TransactionType transactionType,
      TransactionStatus transactionStatus,
      String sourceAccountNumber) {
    this.amount = amount;
    this.transactionType = transactionType;
    this.transactionStatus = transactionStatus;
    this.transactionDate = Instant.now();
    this.sourceAccountNumber = sourceAccountNumber;
    this.targetAccountNumber = "N/A";
  }
}