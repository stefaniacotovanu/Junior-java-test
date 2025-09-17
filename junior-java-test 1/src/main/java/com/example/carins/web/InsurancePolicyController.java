package com.example.carins.web;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.dto.InsurancePolicyRequest;
import com.example.carins.web.dto.InsurancePolicyResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class InsurancePolicyController {

    private final InsurancePolicyService policyService;
    private final CarRepository carRepository;

    public InsurancePolicyController(InsurancePolicyService policyService, CarRepository carRepository) {
        this.policyService = policyService;
        this.carRepository = carRepository;
    }

    @PostMapping
    public ResponseEntity<InsurancePolicyResponse> createPolicy(@RequestBody @Valid InsurancePolicyRequest request) {
        InsurancePolicy policy = new InsurancePolicy();
        policy.setCar(carRepository.findById(request.carId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid car for this policy")));
        policy.setProvider(request.provider());
        policy.setStartDate(request.startDate());
        policy.setEndDate(request.endDate());

        InsurancePolicy saved = policyService.createPolicy(policy);

        InsurancePolicyResponse response = new InsurancePolicyResponse(
                saved.getId(),
                saved.getCar().getId(),
                saved.getProvider(),
                saved.getStartDate(),
                saved.getEndDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsurancePolicy> updatePolicy(@PathVariable Long id, @RequestBody @Valid InsurancePolicy policy) {
        InsurancePolicy updated = policyService.updatePolicy(id, policy);
        return ResponseEntity.ok(updated);
    }
}
