package com.goldtrust.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimAssessmentRepository extends JpaRepository<ClaimAssessment, Long> {
    Optional<ClaimAssessment> findFirstByClaimIdOrderByCreatedAtDesc(Long claimId);
    List<ClaimAssessment> findByClaimId(Long claimId);
}

