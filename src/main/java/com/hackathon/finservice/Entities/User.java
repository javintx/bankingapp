package com.hackathon.finservice.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Entity
public record User(
    @NotEmpty
    String name,

    @Email
    @NotEmpty
    String email,

    @NotEmpty
    @Size(min = 8)
    String password,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID accountNumber,

    @NotEmpty
    String accountType,

    @NotEmpty
    String hashedPassword) {

}