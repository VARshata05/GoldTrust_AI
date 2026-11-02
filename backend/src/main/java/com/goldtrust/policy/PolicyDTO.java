package com.goldtrust.policy;

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
public class PolicyDTO {
    private Long id;
    private String policyNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long productId;
    private String productName;
    private String status;
    private BigDecimal totalSumInsured;
    private BigDecimal annualPremium;
    private String paymentFrequency;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private Integer renewalCount;
    private String policyDocumentUrl;
    private Instant issuedAt;
    private List<PolicyAssetDTO> coveredAssets;
    private List<PolicyCoverageItemDTO> coverages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PolicyAssetDTO {
        private String assetId;
        private String assetName;
        private String category;
        private String purity;
        private BigDecimal netGoldWeight;
        private Integer versionNumber;
        private BigDecimal sumInsured;
        private BigDecimal allocatedPremium;
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PolicyCoverageItemDTO {
        private String coverageCode;
        private String coverageName;
        private BigDecimal coverageLimit;
        private BigDecimal deductibleAmount;
        private BigDecimal premiumCharge;
    }
}

