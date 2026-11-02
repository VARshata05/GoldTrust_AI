package com.goldtrust.valuation;

import com.goldtrust.jewellery.JewelleryAsset;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "valuation_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValuationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gold_rate_id")
    private GoldRate goldRate;

    @Column(name = "net_gold_weight", nullable = false, precision = 10, scale = 3)
    private BigDecimal netGoldWeight;

    @Column(nullable = false, length = 20)
    private String purity;

    @Column(name = "applicable_gold_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal applicableGoldRate;

    @Column(name = "gold_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal goldValue;

    @Column(name = "eligible_stone_value", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal eligibleStoneValue = BigDecimal.ZERO;

    @Column(name = "eligible_making_charges", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal eligibleMakingCharges = BigDecimal.ZERO;

    @Column(name = "current_estimated_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentEstimatedValue;

    @Column(name = "insured_value", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal insuredValue = BigDecimal.ZERO;

    @Column(name = "coverage_gap", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal coverageGap = BigDecimal.ZERO;

    @Column(name = "gap_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal gapPercentage = BigDecimal.ZERO;

    @Column(name = "inflation_adjustment_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal inflationAdjustmentRate = BigDecimal.ZERO;

    @Column(name = "calculated_at")
    @Builder.Default
    private Instant calculatedAt = Instant.now();
}

