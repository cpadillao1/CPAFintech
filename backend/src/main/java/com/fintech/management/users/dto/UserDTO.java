package com.fintech.management.users.dto;

import com.fintech.management.branches.dto.BranchDTO;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;
import java.time.LocalDateTime;

@Data
@ToString
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private Boolean active;
    private BranchDTO branch; // We include the complete branch object (but filtered by its own DTO)
    private LocalDateTime createdAt;
    // Este es el campo que le falta a tu DTO:
    private Integer branchId;
}
