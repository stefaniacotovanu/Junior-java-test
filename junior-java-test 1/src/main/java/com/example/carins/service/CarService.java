package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.CarHistoryEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public Car createCar(Car car) {
        if (carRepository.findByVin(car.getVin()).isPresent()) {
            throw new IllegalArgumentException("Car with VIN " + car.getVin() + " already exists");
        }
        return carRepository.save(car);
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) {
            throw new IllegalArgumentException("carId and date must not be null");
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id " + carId));

        return policyRepository.existsActiveOnDate(carId, date);
    }

    public List<CarHistoryEvent> getCarHistory(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id " + carId));

        List<CarHistoryEvent> events = new ArrayList<>();

        // Polițe
        policyRepository.findByCarId(carId).forEach(p -> {
            events.add(new CarHistoryEvent(
                    p.getStartDate(),
                    "POLICY",
                    p.getProvider() + " (" + p.getStartDate() + " → " + p.getEndDate() + ")"
            ));
        });

        // Claims
        claimRepository.findByCarId(carId).forEach(c -> {
            events.add(new CarHistoryEvent(
                    c.getClaimDate(),
                    "CLAIM",
                    c.getDescription() + " (amount: " + c.getAmount() + ")"
            ));
        });

        events.sort(Comparator.comparing(CarHistoryEvent::date));
        return events;
    }

}

