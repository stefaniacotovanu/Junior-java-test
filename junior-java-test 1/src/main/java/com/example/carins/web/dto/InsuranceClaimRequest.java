package com.example.carins.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsuranceClaimRequest(
        @NotNull LocalDate claimDate,
        @NotBlank String description,
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {}
