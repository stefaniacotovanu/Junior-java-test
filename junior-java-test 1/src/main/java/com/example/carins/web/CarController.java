package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.service.CarNotFoundException;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.CarHistoryEvent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    private final CarRepository carRepository;

    public CarController(CarService service, CarRepository carRepository) {
        this.service = service;
        this.carRepository = carRepository;
    }

    @PostMapping("/cars")
    public ResponseEntity<Car> createCar(@RequestBody @Valid Car car) {
        Car saved = service.createCar(car); // aici se face validarea VIN
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId,
                                              @RequestParam String date) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id " + carId));

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date); // validare format ISO
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format, expected yyyy-MM-dd");
        }

        if (localDate.isBefore(LocalDate.of(1900, 1, 1)) || localDate.isAfter(LocalDate.now().plusYears(50))) {
            throw new IllegalArgumentException("Date is out of supported range");
        }

        boolean valid = service.isInsuranceValid(carId, localDate);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, localDate.toString(), valid));
    }

    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<List<CarHistoryEvent>> getCarHistory(@PathVariable Long carId) {
        List<CarHistoryEvent> history = service.getCarHistory(carId);
        return ResponseEntity.ok(history);
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {
    }
}
