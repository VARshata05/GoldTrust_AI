package com.goldtrust.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimFinancialsRepository extends JpaRepository<ClaimFinancials, Long> {
    Optional<ClaimFinancials> findByClaimId(Long claimId);
}

