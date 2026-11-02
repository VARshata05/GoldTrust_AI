package com.goldtrust.jewellery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnershipEventRepository extends JpaRepository<OwnershipEvent, Long> {
    List<OwnershipEvent> findByJewelleryAssetIdOrderByTransferDateAsc(Long jewelleryAssetId);
}

