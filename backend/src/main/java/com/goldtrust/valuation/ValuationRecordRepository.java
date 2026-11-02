package com.goldtrust.valuation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValuationRecordRepository extends JpaRepository<ValuationRecord, Long> {
    List<ValuationRecord> findByJewelleryAssetIdOrderByCalculatedAtDesc(Long jewelleryAssetId);
    Optional<ValuationRecord> findFirstByJewelleryAssetIdOrderByCalculatedAtDesc(Long jewelleryAssetId);
}

