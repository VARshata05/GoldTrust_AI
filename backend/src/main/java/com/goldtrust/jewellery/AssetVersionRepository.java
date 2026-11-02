package com.goldtrust.jewellery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetVersionRepository extends JpaRepository<AssetVersion, Long> {
    List<AssetVersion> findByJewelleryAssetIdOrderByVersionNumberAsc(Long jewelleryAssetId);
    Optional<AssetVersion> findByJewelleryAssetIdAndVersionNumber(Long jewelleryAssetId, Integer versionNumber);
}

