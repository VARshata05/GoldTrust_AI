package com.goldtrust.policy;

import com.goldtrust.product.CoverageDefinition;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "policy_coverages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyCoverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coverage_id", nullable = false)
    private CoverageDefinition coverage;

    @Column(name = "coverage_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageLimit;

    @Column(name = "deductible_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal deductibleAmount;

    @Column(name = "premium_charge", nullable = false, precision = 15, scale = 2)
    private BigDecimal premiumCharge;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

