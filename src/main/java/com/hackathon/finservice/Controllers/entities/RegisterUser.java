package com.hackathon.finservice.Controllers.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUser(
    @NotBlank
    String name,

    @NotBlank
    @Size(min = 8)
    String password,

    @Email
    @NotBlank
    String email) {

}
