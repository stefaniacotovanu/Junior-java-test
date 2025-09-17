package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsurancePolicyService {
    private final InsurancePolicyRepository policyRepository;
    private final CarRepository carRepository;

    public InsurancePolicyService(InsurancePolicyRepository policyRepository, CarRepository carRepository) {
        this.policyRepository = policyRepository;
        this.carRepository = carRepository;
    }

    public InsurancePolicy createPolicy(InsurancePolicy policy) {
        validatePolicy(policy);
        return policyRepository.save(policy);
    }

    public InsurancePolicy updatePolicy(Long policyId, InsurancePolicy updatedPolicy) {
        InsurancePolicy existing = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found with id " + policyId));

        validatePolicy(updatedPolicy);

        existing.setCar(updatedPolicy.getCar());
        existing.setProvider(updatedPolicy.getProvider());
        existing.setStartDate(updatedPolicy.getStartDate());
        existing.setEndDate(updatedPolicy.getEndDate());

        return policyRepository.save(existing);
    }

    private void validatePolicy(InsurancePolicy policy) {
        if (policy.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (policy.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (policy.getEndDate().isBefore(policy.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        if (policy.getCar() == null || policy.getCar().getId() == null || !carRepository.existsById(policy.getCar().getId())) {
            throw new IllegalArgumentException("Invalid car for this policy");
        }
    }

    @PostConstruct
    public void init() {
        fixOpenEndedPolicies();
    }

    public void fixOpenEndedPolicies() {
        List<InsurancePolicy> policies = policyRepository.findAll();
        for (InsurancePolicy p : policies) {
            if (p.getEndDate() == null) {
                p.setEndDate(p.getStartDate().plusYears(1));
                policyRepository.save(p);
            }
        }
    }

}
