package com.example.carins.web.dto;

import java.time.LocalDate;

public record InsurancePolicyRequest(
        Long carId,
        String provider,
        LocalDate startDate,
        LocalDate endDate
) {
}