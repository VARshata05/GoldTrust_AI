package com.goldtrust.claim;

import com.goldtrust.customer.Customer;
import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jewellery.JewelleryAsset;
import com.goldtrust.policy.InsurancePolicy;
import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_number", nullable = false, unique = true, length = 50)
    private String claimNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "incident_type", nullable = false, length = 50)
    private String incidentType; // LOSS, THEFT, DAMAGE, MYSTERIOUS_DISAPPEARANCE, NATURAL_DISASTER

    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    @Column(name = "incident_location", nullable = false)
    private String incidentLocation;

    @Column(name = "incident_description", nullable = false, columnDefinition = "TEXT")
    private String incidentDescription;

    @Column(name = "police_report_number", length = 100)
    private String policeReportNumber;

    @Column(name = "police_station", length = 150)
    private String policeStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_jeweller_id")
    private Jeweller preferredJeweller;

    @Column(name = "settlement_preference", length = 50)
    @Builder.Default
    private String settlementPreference = "REPAIR"; // REPAIR, REPLACEMENT, CASH_SETTLEMENT

    @Column(name = "claimed_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal claimedAmount;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal approvedAmount = BigDecimal.ZERO;

    @Column(name = "deductible_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal deductibleAmount = BigDecimal.ZERO;

    @Column(name = "net_settlement_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal netSettlementAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "SUBMITTED"; 
    // Workflow: DRAFT, SUBMITTED, VALIDATION, COVERAGE_CHECK, AI_ASSESSMENT, UNDER_REVIEW, ADDITIONAL_INFORMATION_REQUIRED, APPROVED, REJECTED, SETTLEMENT_PENDING, SETTLED, CLOSED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    @Column(name = "submission_date")
    @Builder.Default
    private Instant submissionDate = Instant.now();

    @Column(name = "closure_date")
    private Instant closureDate;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ClaimEvidence> evidenceList = new ArrayList<>();
}

