package com.fintech.core.accounts.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRestrictionDTO {
    private Long id;
    private Long accountId;
    private String accountNumber;      // Ahora sí tendrá setter
    private Long restrictionTypeCatId;
    private String restrictionType;
    private Long reasonCodeCatId;
    private String reasonCode;
    private Long statusCatId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String authorizer;
    private String observations;
    private LocalDateTime createdAt;
    private String releaseAuthorizer;
}

