package com.goldtrust.repair;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairDTO {
    private Long id;
    private String orderNumber;
    private Long claimId;
    private String claimNumber;
    private String assetId;
    private String assetName;
    private Long jewellerId;
    private String jewellerName;
    private String damageDescription;
    private String repairScope;
    private BigDecimal estimatedCost;
    private BigDecimal approvedCost;
    private String status;
    private String jewellerNotes;
    private BigDecimal postRepairWeight;
    private String postRepairImageUrl;
    private Instant completedAt;
    private Instant createdAt;

    public static RepairDTO fromEntity(RepairOrder r) {
        return RepairDTO.builder()
                .id(r.getId())
                .orderNumber(r.getOrderNumber())
                .claimId(r.getClaim().getId())
                .claimNumber(r.getClaim().getClaimNumber())
                .assetId(r.getJewelleryAsset().getAssetId())
                .assetName(r.getJewelleryAsset().getName())
                .jewellerId(r.getJeweller().getId())
                .jewellerName(r.getJeweller().getBusinessName())
                .damageDescription(r.getDamageDescription())
                .repairScope(r.getRepairScope())
                .estimatedCost(r.getEstimatedCost())
                .approvedCost(r.getApprovedCost())
                .status(r.getStatus())
                .jewellerNotes(r.getJewellerNotes())
                .postRepairWeight(r.getPostRepairWeight())
                .postRepairImageUrl(r.getPostRepairImageUrl())
                .completedAt(r.getCompletedAt())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

