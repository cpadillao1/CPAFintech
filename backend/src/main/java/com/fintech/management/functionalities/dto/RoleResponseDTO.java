package com.fintech.management.functionalities.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RoleResponseDTO {
    private UUID roleId;
    private String roleName;
    private String description;
    private List<FunctionalityDTO> functionalities;
}
