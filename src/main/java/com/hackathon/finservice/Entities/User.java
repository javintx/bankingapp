package com.hackathon.finservice.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public record User(
    @Column(nullable = false)
    String name,

    @Column(nullable = false)
    String email,

    @Column(nullable = false)
    String password,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String accountNumber,

    @Column(nullable = false)
    String accountType,

    @Column(nullable = false)
    String hashedPassword) {

}