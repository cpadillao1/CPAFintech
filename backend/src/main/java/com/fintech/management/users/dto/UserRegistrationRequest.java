package com.fintech.management.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserRegistrationRequest {
    @NotBlank(message = "The name is required")
    @Size(min = 2, max = 30, message = "The name must be between 2 and 30 characters long")
    private String firstName;

    @NotBlank(message = "The last name is required")
    @Size(min = 2, max = 30, message = "The last name must be between 2 and 30 characters long")
    private String lastName;

    private String login;

    @NotBlank(message = "An email address is required")
    @Email(message = "You must provide a valid email address")
    private String email;

    @NotBlank(message = "A password is required")
    @Size(min = 8, message = "The password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "The password must contain at least one uppercase letter, one lowercase letter, and one number")
    private String password;

    @NotBlank(message = "The branch code is required")
    @Size(min = 4, max = 4, message = "The branch code must be exactly 4 characters long")
    private String branchCode;

    private List<String> roleNames; // <-- New list of role names (ej: ["USER", "AUDITOR"])
}
