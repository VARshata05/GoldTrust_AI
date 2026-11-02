package com.goldtrust.repair;

import com.goldtrust.claim.Claim;
import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jewellery.JewelleryAsset;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "repair_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jeweller_id", nullable = false)
    private Jeweller jeweller;

    @Column(name = "damage_description", nullable = false, columnDefinition = "TEXT")
    private String damageDescription;

    @Column(name = "repair_scope", columnDefinition = "TEXT")
    private String repairScope;

    @Column(name = "estimated_cost", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Column(name = "approved_cost", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal approvedCost = BigDecimal.ZERO;

    @Column(length = 50)
    @Builder.Default
    private String status = "ESTIMATE_PENDING"; 
    // ESTIMATE_PENDING, ESTIMATE_SUBMITTED, ESTIMATE_APPROVED, IN_PROGRESS, COMPLETED, VERIFIED

    @Column(name = "estimate_submitted_at")
    private Instant estimateSubmittedAt;

    @Column(name = "estimate_approved_at")
    private Instant estimateApprovedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "jeweller_notes", columnDefinition = "TEXT")
    private String jewellerNotes;

    @Column(name = "post_repair_weight", precision = 10, scale = 3)
    private BigDecimal postRepairWeight;

    @Column(name = "post_repair_image_url", length = 500)
    private String postRepairImageUrl;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

