package com.goldtrust.policy;

import com.goldtrust.jewellery.JewelleryAsset;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "policy_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @Column(name = "asset_version_number", nullable = false)
    @Builder.Default
    private Integer assetVersionNumber = 1;

    @Column(name = "sum_insured", nullable = false, precision = 15, scale = 2)
    private BigDecimal sumInsured;

    @Column(name = "allocated_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal allocatedPremium;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

