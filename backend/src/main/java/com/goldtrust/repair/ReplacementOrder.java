package com.goldtrust.repair;

import com.goldtrust.claim.Claim;
import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jewellery.JewelleryAsset;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "replacement_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplacementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_asset_id", nullable = false)
    private JewelleryAsset originalAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replacement_asset_id")
    private JewelleryAsset replacementAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jeweller_id", nullable = false)
    private Jeweller jeweller;

    @Column(name = "approved_replacement_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal approvedReplacementValue;

    @Column(name = "actual_replacement_value", precision = 15, scale = 2)
    private BigDecimal actualReplacementValue;

    @Column(name = "replacement_specs", columnDefinition = "TEXT")
    private String replacementSpecs;

    @Column(length = 50)
    @Builder.Default
    private String status = "SOURCING"; // SOURCING, READY_FOR_INSPECTION, INSPECTED, COMPLETED

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

