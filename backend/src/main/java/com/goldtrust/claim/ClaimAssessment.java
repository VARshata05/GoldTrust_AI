package com.goldtrust.claim;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "claim_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessor_user_id")
    private User assessorUser;

    @Column(name = "policy_validity_confirmed", nullable = false)
    @Builder.Default
    private boolean policyValidityConfirmed = true;

    @Column(name = "coverage_confirmed", nullable = false)
    @Builder.Default
    private boolean coverageConfirmed = true;

    @Column(name = "applicable_coverage_code", length = 50)
    private String applicableCoverageCode;

    @Column(name = "deductible_percentage_applied", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal deductiblePercentageApplied = BigDecimal.valueOf(5.00);

    @Column(name = "identity_consistency_status", length = 50)
    private String identityConsistencyStatus; // CONSISTENT, VERIFIED_TRANSFORMATION, REVIEW_REQUIRED, IDENTITY_INCONSISTENCY

    @Column(name = "anomaly_detected")
    @Builder.Default
    private Boolean anomalyDetected = false;

    @Column(name = "anomaly_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal anomalyScore = BigDecimal.ZERO;

    @Column(name = "ai_confidence_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aiConfidenceScore = BigDecimal.ZERO;

    @Column(nullable = false, length = 50)
    private String recommendation; // APPROVE_REPAIR, APPROVE_REPLACEMENT, APPROVE_CASH, REFER_TO_SENIOR, REQUEST_ADDITIONAL_EVIDENCE, DECLINE

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "assessment_report_json", columnDefinition = "TEXT")
    private String assessmentReportJson;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

