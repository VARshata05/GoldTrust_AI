package com.goldtrust.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Claim> findByPolicyId(Long policyId);
    List<Claim> findByStatusOrderByCreatedAtDesc(String status);
}

