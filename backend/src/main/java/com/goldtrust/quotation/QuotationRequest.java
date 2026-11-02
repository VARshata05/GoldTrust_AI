package com.goldtrust.quotation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotEmpty(message = "At least one jewellery asset ID must be selected")
    private List<String> assetIds;

    private List<String> selectedCoverageCodes;

    private BigDecimal customInsuredValue; // Optional override; defaults to asset sum
    private BigDecimal deductiblePercentage; // e.g. 2.50, 5.00, 10.00
    private String stateOrRegion;
}

