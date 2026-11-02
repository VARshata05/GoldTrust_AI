package com.goldtrust.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverageDTO {
    private Long id;
    private String coverageCode;
    private String coverageName;
    private String description;
    private BigDecimal coverageLimitPercentage;
    private BigDecimal defaultDeductiblePercentage;
    private BigDecimal premiumFactor;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private boolean enabled;
    private boolean mandatory;
    private boolean defaultSelected;

    public static CoverageDTO fromEntity(CoverageDefinition c) {
        return CoverageDTO.builder()
                .id(c.getId())
                .coverageCode(c.getCoverageCode())
                .coverageName(c.getCoverageName())
                .description(c.getDescription())
                .coverageLimitPercentage(c.getCoverageLimitPercentage())
                .defaultDeductiblePercentage(c.getDefaultDeductiblePercentage())
                .premiumFactor(c.getPremiumFactor())
                .effectiveDate(c.getEffectiveDate())
                .expirationDate(c.getExpirationDate())
                .enabled(c.isEnabled())
                .build();
    }
}

