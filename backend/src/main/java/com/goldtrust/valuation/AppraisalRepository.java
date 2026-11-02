package com.goldtrust.valuation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    List<Appraisal> findByJewelleryAssetIdOrderByAppraisalDateDesc(Long jewelleryAssetId);
}

