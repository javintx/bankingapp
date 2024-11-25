package com.hackathon.finservice.Entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "users")
public record User(
    @Column(nullable = false)
    String name,

    @Id
    @Column(nullable = false)
    String email,

    @Column(nullable = false)
    String password,

    @Column(nullable = false)
    String hashedPassword,

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Account> accounts) {

}