package com.hackathon.finservice.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public record Account(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String accountNumber,

    @Column(nullable = false)
    double balance,

    @Column(nullable = false)
    AccountType accountType,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user) {

    public static Account createMainAccount(User user) {
        return new Account(UUID.randomUUID().toString(), 0.0d, AccountType.MAIN, user);
    }

    public static Account createInvestAccount(User user) {
        return new Account(UUID.randomUUID().toString(), 0.0d, AccountType.INVEST, user);
    }
}