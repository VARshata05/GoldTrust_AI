package com.goldtrust.product;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insurance_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_rate_percentage", nullable = false, precision = 6, scale = 4)
    @Builder.Default
    private BigDecimal baseRatePercentage = BigDecimal.valueOf(0.0150);

    @Column(name = "min_sum_insured", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal minSumInsured = BigDecimal.valueOf(10000.00);

    @Column(name = "max_sum_insured", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal maxSumInsured = BigDecimal.valueOf(10000000.00);

    @Column(name = "tenure_months")
    @Builder.Default
    private Integer tenureMonths = 12;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductCoverage> productCoverages = new ArrayList<>();
}

