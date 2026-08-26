package com.fintech.management.users.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder // <--- Esta es la clave para el error del Service
public class LoginResponse {
    private String token;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private Integer branchId;
    private String branchName;
    private List<String> roles; // Cambiamos a List para manejar tus múltiples roles
    private String businessDate;
    private String systemStatus;
}
