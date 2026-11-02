package com.goldtrust.underwriting;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "underwriting_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderwritingAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private InsuranceApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id")
    private User officer;

    @Column(nullable = false, length = 50)
    private String decision; // APPROVE, REFER, DECLINE, REQUEST_INFORMATION

    @Column(name = "jewellery_verification_score", precision = 5, scale = 2)
    private BigDecimal jewelleryVerificationScore;

    @Column(name = "appraisal_validity_score", precision = 5, scale = 2)
    private BigDecimal appraisalValidityScore;

    @Column(name = "customer_risk_score", precision = 5, scale = 2)
    private BigDecimal customerRiskScore;

    @Column(name = "ai_risk_assessment_score", precision = 5, scale = 2)
    private BigDecimal aiRiskAssessmentScore;

    @Column(name = "overall_risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallRiskScore;

    @Column(name = "decision_reasons", nullable = false, columnDefinition = "TEXT")
    private String decisionReasons;

    @Column(name = "risk_indicators_json", columnDefinition = "TEXT")
    private String riskIndicatorsJson;

    @Column(columnDefinition = "TEXT")
    private String stipulations;

    @Column(name = "assessed_at")
    @Builder.Default
    private Instant assessedAt = Instant.now();
}

