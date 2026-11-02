package com.goldtrust.repair;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRecordRepository extends JpaRepository<SettlementRecord, Long> {
    Optional<SettlementRecord> findBySettlementReference(String settlementReference);
    List<SettlementRecord> findByClaimId(Long claimId);
}

