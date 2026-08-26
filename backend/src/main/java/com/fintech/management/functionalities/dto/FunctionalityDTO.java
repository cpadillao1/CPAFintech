package com.fintech.management.functionalities.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FunctionalityDTO {
    private UUID id;
    private String name;
    private String code;
    private String label;
    private String type;
    private String icon;
    private Integer sortOrder;
    private UUID parentId;

    private List<FunctionalityDTO> children;
}
