package com.goldtrust.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimDecisionRepository extends JpaRepository<ClaimDecision, Long> {
    Optional<ClaimDecision> findFirstByClaimIdOrderByDecisionDateDesc(Long claimId);
}

