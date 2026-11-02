package com.goldtrust.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

public class AiDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentityCompareRequest {
        private String originalImageUrl;
        private String currentImageUrl;
        private String assetId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TageAssessmentRequest {
        private String assetId;
        private Long claimId;
        private BigDecimal currentWeight;
        private BigDecimal originalWeight;
        private String purity;
        private String huid;
        private Integer transformationCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TageAssessmentResponse {
        private String assessmentReference;
        private String status; // CONSISTENT, VERIFIED_TRANSFORMATION, REVIEW_REQUIRED, IDENTITY_INCONSISTENCY
        private BigDecimal visualSimilarity;
        private BigDecimal physicalConsistency;
        private BigDecimal transformationConsistency;
        private BigDecimal ownershipContinuity;
        private BigDecimal evidenceCompleteness;
        private BigDecimal anomalyScore;
        private boolean anomalyDetected;
        private String recommendedAction;
        private String explanation;
        private String modelVersion;
        private boolean isExperimental;
        private Map<String, Object> featureAttributions;
    }
}

