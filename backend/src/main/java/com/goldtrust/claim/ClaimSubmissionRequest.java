package com.goldtrust.claim;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSubmissionRequest {
    @NotNull(message = "Policy ID is required")
    private Long policyId;

    @NotBlank(message = "Jewellery Asset ID is required")
    private String assetId;

    @NotBlank(message = "Incident type is required (e.g. LOSS, THEFT, DAMAGE, MYSTERIOUS_DISAPPEARANCE, NATURAL_DISASTER)")
    private String incidentType;

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    @NotBlank(message = "Incident location is required")
    private String incidentLocation;

    @NotBlank(message = "Incident description is required")
    private String incidentDescription;

    private String policeReportNumber;
    private String policeStation;
    private Long preferredJewellerId;
    private String settlementPreference; // REPAIR, REPLACEMENT, CASH_SETTLEMENT

    @NotNull(message = "Claimed amount is required")
    @DecimalMin(value = "0.01", message = "Claimed amount must be greater than zero")
    private BigDecimal claimedAmount;

    private List<EvidenceItem> evidence;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceItem {
        private String evidenceType;
        private String fileName;
        private String fileUrl;
        private String description;
    }
}

