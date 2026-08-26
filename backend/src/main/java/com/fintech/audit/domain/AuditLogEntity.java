package com.fintech.audit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String username;    // Quién lo hizo
    private String action;      // Qué hizo (EJ: CREATE_BRANCH)
    private String module;      // En qué módulo (EJ: BRANCHES)
    private String status;
    private String detail;      // Detalle o ID del objeto afectado
    private String ipAddress;   // Desde dónde (IP del cliente)
    private LocalDate auditDate;
    private LocalDateTime timestamp;
}

