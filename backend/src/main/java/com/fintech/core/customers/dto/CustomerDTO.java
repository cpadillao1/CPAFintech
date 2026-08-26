package com.fintech.core.customers.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CustomerDTO(
        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @NotNull(message = "El tipo de documento es obligatorio")
        Long documentTypeId,

        @NotBlank(message = "El número de documento es obligatorio")
        String documentNumber,

        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate birthDate,

        @NotNull(message = "El genero es obligatorio")
        Long genderId,

        @NotNull(message = "El estado del cliente es obligatorio")
        Long statusId,

        @NotNull(message = "El estado civil del cliente es obligatorio")
        Long maritalStatusId,

        @NotNull(message = "La nacionalidad del cliente es obligatorio")
        Integer nationalityId,

        @NotBlank(message = "El usuario creador es obligatorio")
        String createdBy,

        @Valid
        List<CustomerAddressDTO> addresses,

        @Valid
        List<CustomerPhoneDTO> phones,

        @Valid
        List<CustomerEmailDTO> emails
) {}
