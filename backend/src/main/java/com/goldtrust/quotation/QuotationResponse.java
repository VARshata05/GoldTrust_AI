package com.goldtrust.quotation;

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
public class QuotationResponse {
    private Long id;
    private String quotationNumber;
    private Long productId;
    private String productName;
    private BigDecimal totalInsuredValue;
    private BigDecimal deductiblePercentage;
    private BigDecimal baseRate;
    private BigDecimal basePremium;
    private BigDecimal riskFactor;
    private BigDecimal coverageFactor;
    private BigDecimal locationFactor;
    private BigDecimal annualPremium;
    private BigDecimal monthlyEquivalent;
    private List<String> selectedCoverages;
    private List<QuotationItemDetail> items;
    private Instant validUntil;
    private String disclaimer;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotationItemDetail {
        private String assetId;
        private String assetName;
        private String category;
        private BigDecimal netGoldWeight;
        private BigDecimal insuredValue;
        private BigDecimal allocatedPremium;
    }
}

