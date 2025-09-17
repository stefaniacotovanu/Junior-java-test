package com.example.carins.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsuranceClaimResponse(
        Long id,
        Long carId,
        LocalDate claimDate,
        String description,
        BigDecimal amount
) {
}
