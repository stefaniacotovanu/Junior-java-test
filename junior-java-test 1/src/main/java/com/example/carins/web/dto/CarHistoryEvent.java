package com.example.carins.web.dto;

import java.time.LocalDate;

public record CarHistoryEvent(
        LocalDate date,
        String type, // "POLICY" or "CLAIM"
        String description
) {}