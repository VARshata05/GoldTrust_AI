package com.goldtrust.valuation;

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
public class ValuationDTO {
    private Long id;
    private String assetId;
    private String assetName;
    private BigDecimal netGoldWeight;
    private String purity;
    private BigDecimal applicableGoldRate;
    private BigDecimal goldValue;
    private BigDecimal eligibleStoneValue;
    private BigDecimal eligibleMakingCharges;
    private BigDecimal currentEstimatedValue;
    private BigDecimal insuredValue;
    private BigDecimal coverageGap;
    private BigDecimal gapPercentage;
    private BigDecimal inflationAdjustmentRate;
    private boolean gapIdentified;
    private String recommendation;
    private Instant calculatedAt;
}

