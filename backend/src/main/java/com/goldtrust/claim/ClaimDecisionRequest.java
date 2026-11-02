package com.goldtrust.claim;

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
public class ClaimDecisionRequest {
    @NotBlank(message = "Decision is required (APPROVED, REJECTED, REQUEST_INFORMATION)")
    private String decision;

    @NotBlank(message = "Decision rationale is required")
    private String decisionRationale;

    private String settlementMethod; // REPAIR, REPLACEMENT, CASH
    private BigDecimal approvedAmount;
}

