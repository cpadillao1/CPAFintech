package com.fintech.management.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record LoginRequest (
    @NotBlank(message = "Login is required")
    String login,

    @NotBlank(message = "Password is required")
    String password,

    @NotNull(message = "Branch is required")
    @Positive(message = "Branch must be a valid identifier")
    Integer branchId
)
{}
