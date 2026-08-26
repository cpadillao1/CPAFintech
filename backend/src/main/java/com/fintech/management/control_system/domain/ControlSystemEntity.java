package com.fintech.management.control_system.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "control_system")
@Getter
@Setter
public class ControlSystemEntity {

    @Id
    private Integer id = 1;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "before_business_date", nullable = false)
    private LocalDate beforeBusinessDate;

    @Column(name = "after_business_date", nullable = false)
    private LocalDate afterBusinessDate;

    @Column(nullable = false, length = 20)
    private String status;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Version
    private Long version; // Optimistic Locking para seguridad transaccional

    private Long currentJobExecutionId;

    private LocalDateTime eodStartAt;

    @Column(name = "is_month_end", nullable = false)
    private boolean isMonthEnd = false;

}
