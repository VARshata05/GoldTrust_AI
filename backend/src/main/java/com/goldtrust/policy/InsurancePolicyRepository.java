package com.goldtrust.policy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    List<InsurancePolicy> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<InsurancePolicy> findByStatus(String status);
}

