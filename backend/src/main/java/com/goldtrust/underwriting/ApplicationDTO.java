package com.goldtrust.underwriting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String applicationNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String quotationNumber;
    private Long productId;
    private String productName;
    private BigDecimal totalInsuredValue;
    private BigDecimal proposedPremium;
    private String status;
    private String customerNotes;
    private String underwritingDecision;
    private Long assignedOfficerId;
    private String assignedOfficerName;
    private Instant decisionDate;
    private Instant createdAt;
    private List<UnderwritingAssessmentDTO> assessments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnderwritingAssessmentDTO {
        private Long id;
        private String decision;
        private BigDecimal jewelleryVerificationScore;
        private BigDecimal appraisalValidityScore;
        private BigDecimal customerRiskScore;
        private BigDecimal aiRiskAssessmentScore;
        private BigDecimal overallRiskScore;
        private String decisionReasons;
        private String stipulations;
        private Instant assessedAt;
    }
}

