package com.fintech.management.control_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatusDTO {

    private Integer id;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate businessDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate beforeBusinessDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate afterBusinessDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastUpdate;

    private String status;             // "ACTIVE", "IN_CLOSING", etc.
    private boolean isReadOnly;        // Útil para bloquear botones en el FE si está en cierre
    private Long version;
    private boolean isMonthEnd;
}

