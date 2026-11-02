package com.goldtrust.product;

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
public class ProductDTO {
    private Long id;
    private String productCode;
    private String productName;
    private String description;
    private BigDecimal baseRatePercentage;
    private BigDecimal minSumInsured;
    private BigDecimal maxSumInsured;
    private Integer tenureMonths;
    private boolean active;
    private List<CoverageDTO> coverages;

    public static ProductDTO fromEntity(InsuranceProduct p) {
        return ProductDTO.builder()
                .id(p.getId())
                .productCode(p.getProductCode())
                .productName(p.getProductName())
                .description(p.getDescription())
                .baseRatePercentage(p.getBaseRatePercentage())
                .minSumInsured(p.getMinSumInsured())
                .maxSumInsured(p.getMaxSumInsured())
                .tenureMonths(p.getTenureMonths())
                .active(p.isActive())
                .build();
    }
}

