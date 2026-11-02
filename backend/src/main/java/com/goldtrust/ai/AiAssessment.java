package com.goldtrust.ai;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ai_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assessment_reference", nullable = false, unique = true, length = 50)
    private String assessmentReference;

    @Column(name = "target_entity_type", nullable = false, length = 50)
    private String targetEntityType; // JEWELLERY, CLAIM, UNDERWRITING

    @Column(name = "target_entity_id", nullable = false)
    private Long targetEntityId;

    @Column(name = "model_name", nullable = false, length = 100)
    @Builder.Default
    private String modelName = "TAGE-v1.0-Experimental";

    @Column(name = "model_version", nullable = false, length = 50)
    @Builder.Default
    private String modelVersion = "1.0.0";

    @Column(name = "is_experimental", nullable = false)
    @Builder.Default
    private boolean experimental = true;

    @Column(name = "visual_similarity_score", precision = 5, scale = 2)
    private BigDecimal visualSimilarityScore;

    @Column(name = "physical_consistency_score", precision = 5, scale = 2)
    private BigDecimal physicalConsistencyScore;

    @Column(name = "transformation_consistency_score", precision = 5, scale = 2)
    private BigDecimal transformationConsistencyScore;

    @Column(name = "ownership_continuity_score", precision = 5, scale = 2)
    private BigDecimal ownershipContinuityScore;

    @Column(name = "evidence_completeness_score", precision = 5, scale = 2)
    private BigDecimal evidenceCompletenessScore;

    @Column(name = "anomaly_score", precision = 5, scale = 2)
    private BigDecimal anomalyScore;

    @Column(name = "anomaly_detected")
    @Builder.Default
    private Boolean anomalyDetected = false;

    @Column(nullable = false, length = 50)
    private String recommendation; // CONSISTENT, VERIFIED_TRANSFORMATION, REVIEW_REQUIRED, IDENTITY_INCONSISTENCY, INSUFFICIENT_EVIDENCE

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "features_breakdown_json", columnDefinition = "TEXT")
    private String featuresBreakdownJson;

    @Column(name = "shap_contributions_json", columnDefinition = "TEXT")
    private String shapContributionsJson;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

