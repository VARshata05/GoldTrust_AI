package com.goldtrust.jewellery;

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
public class AssetVersionDTO {
    private Long id;
    private Integer versionNumber;
    private String name;
    private String category;
    private String purity;
    private BigDecimal netGoldWeight;
    private BigDecimal grossWeight;
    private String huid;
    private String hallmarkStatus;
    private BigDecimal estimatedValue;
    private String primaryImageUrl;
    private String changeReason;
    private String createdBy;
    private Instant createdAt;

    public static AssetVersionDTO fromEntity(AssetVersion v) {
        return AssetVersionDTO.builder()
                .id(v.getId())
                .versionNumber(v.getVersionNumber())
                .name(v.getName())
                .category(v.getCategory())
                .purity(v.getPurity())
                .netGoldWeight(v.getNetGoldWeight())
                .grossWeight(v.getGrossWeight())
                .huid(v.getHuid())
                .hallmarkStatus(v.getHallmarkStatus())
                .estimatedValue(v.getEstimatedValue())
                .primaryImageUrl(v.getPrimaryImageUrl())
                .changeReason(v.getChangeReason())
                .createdBy(v.getCreatedBy())
                .createdAt(v.getCreatedAt())
                .build();
    }
}

