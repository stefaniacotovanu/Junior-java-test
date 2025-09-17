package com.example.carins.web;

import com.example.carins.model.InsuranceClaim;
import com.example.carins.service.InsuranceClaimService;
import com.example.carins.web.dto.InsuranceClaimRequest;
import com.example.carins.web.dto.InsuranceClaimResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/cars")
public class InsuranceClaimController {
    private final InsuranceClaimService claimService;

    public InsuranceClaimController(InsuranceClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/{carId}/claims")
    public ResponseEntity<InsuranceClaimResponse> registerClaim(
            @PathVariable Long carId,
            @RequestBody @Valid InsuranceClaimRequest request) {

        InsuranceClaim claim = new InsuranceClaim();
        claim.setClaimDate(request.claimDate());
        claim.setDescription(request.description());
        claim.setAmount(request.amount());

        InsuranceClaim saved = claimService.registerClaim(carId, claim);

        InsuranceClaimResponse response = new InsuranceClaimResponse(
                saved.getId(),
                saved.getCar().getId(),
                saved.getClaimDate(),
                saved.getDescription(),
                saved.getAmount()
        );

        return ResponseEntity
                .created(URI.create("/api/cars/" + carId + "/claims/" + saved.getId()))
                .body(response);
    }
}
