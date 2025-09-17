package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.stereotype.Service;

@Service
public class InsuranceClaimService {
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;
    private final CarRepository carRepository;

    public InsuranceClaimService(InsurancePolicyRepository policyRepository, InsuranceClaimRepository claimRepository, CarRepository carRepository) {
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
        this.carRepository = carRepository;
    }

    public InsuranceClaim registerClaim(Long carId, InsuranceClaim claim) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid car id: " + carId));

        boolean validPolicy = policyRepository.existsActiveOnDate(carId, claim.getClaimDate());
        if (!validPolicy) {
            throw new IllegalArgumentException("No active insurance policy for this car on " + claim.getClaimDate());
        }

        claim.setCar(car);
        return claimRepository.save(claim);
    }

}
