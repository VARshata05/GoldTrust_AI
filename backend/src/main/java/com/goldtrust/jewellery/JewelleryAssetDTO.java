package com.goldtrust.jewellery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JewelleryAssetDTO {
    private Long id;
    private String assetId;
    private Long customerId;
    private String customerName;
    private String name;
    private String category;
    private String purity;
    private BigDecimal netGoldWeight;
    private BigDecimal grossWeight;
    private String huid;
    private String hallmarkStatus;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private BigDecimal appraisalValue;
    private BigDecimal currentEstimatedValue;
    private String primaryImageUrl;
    private String ownershipStatus;
    private String verificationStatus;
    private Long verifiedByJewellerId;
    private String verifiedByJewellerName;
    private Instant verifiedAt;
    private Integer currentVersion;
    private Instant registrationDate;
    private List<AssetVersionDTO> versions;
    private List<TransformationDTO> transformations;

    public static JewelleryAssetDTO fromEntity(JewelleryAsset asset) {
        return JewelleryAssetDTO.builder()
                .id(asset.getId())
                .assetId(asset.getAssetId())
                .customerId(asset.getCustomer() != null ? asset.getCustomer().getId() : null)
                .customerName(asset.getCustomer() != null && asset.getCustomer().getUser() != null ?
                        asset.getCustomer().getUser().getFirstName() + " " + asset.getCustomer().getUser().getLastName() : null)
                .name(asset.getName())
                .category(asset.getCategory())
                .purity(asset.getPurity())
                .netGoldWeight(asset.getNetGoldWeight())
                .grossWeight(asset.getGrossWeight())
                .huid(asset.getHuid())
                .hallmarkStatus(asset.getHallmarkStatus())
                .purchaseDate(asset.getPurchaseDate())
                .purchasePrice(asset.getPurchasePrice())
                .appraisalValue(asset.getAppraisalValue())
                .currentEstimatedValue(asset.getCurrentEstimatedValue())
                .primaryImageUrl(asset.getPrimaryImageUrl())
                .ownershipStatus(asset.getOwnershipStatus())
                .verificationStatus(asset.getVerificationStatus())
                .verifiedByJewellerId(asset.getVerifiedByJeweller() != null ? asset.getVerifiedByJeweller().getId() : null)
                .verifiedByJewellerName(asset.getVerifiedByJeweller() != null ? asset.getVerifiedByJeweller().getBusinessName() : null)
                .verifiedAt(asset.getVerifiedAt())
                .currentVersion(asset.getCurrentVersion())
                .registrationDate(asset.getRegistrationDate())
                .build();
    }
}

