package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class PolicyExpiryLoggerService {

    private static final Logger log = LoggerFactory.getLogger(PolicyExpiryLoggerService.class);

    private final InsurancePolicyRepository policyRepository;

    private final Set<Long> alreadyLogged = new HashSet<>();

    public PolicyExpiryLoggerService(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void logExpiredPolicies() {
        List<InsurancePolicy> policies = policyRepository.findAll();

        for (InsurancePolicy p : policies) {
            if (p.getEndDate() == null || alreadyLogged.contains(p.getId())) {
                continue;
            }

            LocalDateTime expiryDateTime = p.getEndDate().atStartOfDay();
            LocalDateTime oneHourAfterExpiry = expiryDateTime.plusHours(1);
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(expiryDateTime) && now.isBefore(oneHourAfterExpiry)) {
                log.info("Policy {} for car {} expired on {}", p.getId(), p.getCar().getId(), p.getEndDate());
                alreadyLogged.add(p.getId());
            }
        }
    }

}
