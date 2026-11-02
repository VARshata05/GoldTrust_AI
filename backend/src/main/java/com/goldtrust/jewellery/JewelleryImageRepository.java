package com.goldtrust.jewellery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JewelleryImageRepository extends JpaRepository<JewelleryImage, Long> {
    List<JewelleryImage> findByJewelleryAssetId(Long jewelleryAssetId);
    List<JewelleryImage> findByJewelleryAssetIdAndVersionNumber(Long jewelleryAssetId, Integer versionNumber);
}

