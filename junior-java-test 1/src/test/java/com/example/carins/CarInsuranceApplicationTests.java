package com.example.carins;

import com.example.carins.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

    @Test
    void insuranceValidityCarNotFound() {
        Exception ex = assertThrows(RuntimeException.class, () -> service.isInsuranceValid(999L, LocalDate.parse("2025-01-01")));
        assertTrue(ex.getMessage().contains("Car not found"));
    }

    @Test
    void insuranceValidityInvalidDate() {
        assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2025-99-99"));
    }

    @Test
    void insuranceValidityNullDate() {
        assertThrows(IllegalArgumentException.class, () -> service.isInsuranceValid(1L, null));
    }

    @Test
    void insuranceValidityNullCarId() {
        assertThrows(IllegalArgumentException.class, () -> service.isInsuranceValid(null, LocalDate.parse("2025-01-01")));
    }

}
