package com.goldtrust.jewellery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransformationEventRepository extends JpaRepository<TransformationEvent, Long> {
    List<TransformationEvent> findByJewelleryAssetIdOrderByCreatedAtAsc(Long jewelleryAssetId);
}

