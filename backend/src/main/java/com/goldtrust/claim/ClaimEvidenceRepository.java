package com.goldtrust.claim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimEvidenceRepository extends JpaRepository<ClaimEvidence, Long> {
    List<ClaimEvidence> findByClaimId(Long claimId);
}

