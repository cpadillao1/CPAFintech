package com.fintech.core.accounts.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountRestrictionReleaseRequest(
        @NotBlank String observations,
        @NotNull Long statusReleasedId,
        @NotBlank String releaseAuthorizer
) {}

