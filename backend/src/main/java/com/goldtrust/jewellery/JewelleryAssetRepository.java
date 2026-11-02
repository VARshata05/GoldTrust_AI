package com.goldtrust.jewellery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JewelleryAssetRepository extends JpaRepository<JewelleryAsset, Long> {
    Optional<JewelleryAsset> findByAssetId(String assetId);
    List<JewelleryAsset> findByCustomerId(Long customerId);
    List<JewelleryAsset> findByVerificationStatus(String verificationStatus);
    Optional<JewelleryAsset> findByHuid(String huid);
    Boolean existsByHuid(String huid);

    @Query("SELECT COUNT(j) FROM JewelleryAsset j")
    long countTotalAssets();
}

