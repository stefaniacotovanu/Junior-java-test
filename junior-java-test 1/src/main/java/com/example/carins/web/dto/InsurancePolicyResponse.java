package com.example.carins.web.dto;

import java.time.LocalDate;

public record InsurancePolicyResponse(
        Long id,
        Long carId,
        String provider,
        LocalDate startDate,
        LocalDate endDate
) {}
