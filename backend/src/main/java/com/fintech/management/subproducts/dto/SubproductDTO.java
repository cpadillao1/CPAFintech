package com.fintech.management.subproducts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SubproductDTO(
        Integer id,

        Integer productId,

        String productName,

        @JsonProperty("interestGroupId")
        Integer interestGroupId, // Este es el que usará el Popup

        @NotBlank(message = "El código del subproducto es obligatorio")
        String code,

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        String description,

        boolean generatesInterest,

        String statementFrequency,

        String status,

        //@NotBlank(message = "|Sequence is mandatory")
        String accountSequenceId
) {}