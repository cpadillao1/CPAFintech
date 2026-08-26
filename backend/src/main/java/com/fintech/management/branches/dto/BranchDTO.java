package com.fintech.management.branches.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class BranchDTO {
    private String code;
    private String name;
    private String address;
    private String city;
    private String country;
    private LocalDateTime createdAt;
}
