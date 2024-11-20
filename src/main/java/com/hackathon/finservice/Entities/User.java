package com.hackathon.finservice.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Entity
public record User(
    @NotBlank
    String name,

    @Email
    @NotBlank
    String email,

    @NotBlank
    @Size(min = 8)
    String password,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID accountNumber,

    String accountType,
    String hashedPassword) {

}