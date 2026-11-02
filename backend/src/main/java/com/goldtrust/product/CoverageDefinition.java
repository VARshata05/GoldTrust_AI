package com.goldtrust.product;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "coverage_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoverageDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coverage_code", nullable = false, unique = true, length = 50)
    private String coverageCode;

    @Column(name = "coverage_name", nullable = false, length = 100)
    private String coverageName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "coverage_limit_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal coverageLimitPercentage = BigDecimal.valueOf(100.00);

    @Column(name = "default_deductible_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal defaultDeductiblePercentage = BigDecimal.valueOf(5.00);

    @Column(name = "premium_factor", nullable = false, precision = 6, scale = 4)
    @Builder.Default
    private BigDecimal premiumFactor = BigDecimal.valueOf(0.0020);

    @Column(name = "eligibility_rules", columnDefinition = "TEXT")
    private String eligibilityRules;

    @Column(name = "effective_date", nullable = false)
    @Builder.Default
    private LocalDate effectiveDate = LocalDate.now();

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();
}

