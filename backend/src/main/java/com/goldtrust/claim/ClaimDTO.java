package com.goldtrust.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO {
    private Long id;
    private String claimNumber;
    private Long policyId;
    private String policyNumber;
    private String assetId;
    private String assetName;
    private String assetCategory;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String incidentType;
    private LocalDate incidentDate;
    private String incidentLocation;
    private String incidentDescription;
    private String policeReportNumber;
    private String policeStation;
    private Long preferredJewellerId;
    private String preferredJewellerName;
    private String settlementPreference;
    private BigDecimal claimedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal deductibleAmount;
    private BigDecimal netSettlementAmount;
    private String status;
    private Long assignedOfficerId;
    private String assignedOfficerName;
    private Instant submissionDate;
    private Instant closureDate;
    private List<ClaimEvidenceDTO> evidenceList;
    private ClaimAssessmentDTO assessment;
    private ClaimDecisionDTO decision;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimEvidenceDTO {
        private Long id;
        private String evidenceType;
        private String fileName;
        private String fileUrl;
        private String description;
        private String uploadedBy;
        private Boolean verified;
        private Instant uploadedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimAssessmentDTO {
        private Long id;
        private boolean policyValidityConfirmed;
        private boolean coverageConfirmed;
        private String applicableCoverageCode;
        private BigDecimal deductiblePercentageApplied;
        private String identityConsistencyStatus;
        private Boolean anomalyDetected;
        private BigDecimal anomalyScore;
        private BigDecimal aiConfidenceScore;
        private String recommendation;
        private String rationale;
        private String assessmentReportJson;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimDecisionDTO {
        private Long id;
        private String decision;
        private String decisionRationale;
        private String settlementMethod;
        private BigDecimal finalSettlementAmount;
        private Instant decisionDate;
        private String officerName;
    }
}

