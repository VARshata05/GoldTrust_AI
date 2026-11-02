package com.goldtrust.underwriting;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingDecisionRequest {
    @NotBlank(message = "Decision is required (APPROVE, REFER, DECLINE, REQUEST_INFORMATION)")
    private String decision;

    @NotBlank(message = "Decision reasons are required")
    private String reasons;

    private String stipulations;
    private BigDecimal jewelleryVerificationScore;
    private BigDecimal appraisalValidityScore;
    private BigDecimal customerRiskScore;
    private BigDecimal aiRiskAssessmentScore;
}

