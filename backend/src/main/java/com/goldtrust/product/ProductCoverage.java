package com.goldtrust.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_coverages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCoverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coverage_id", nullable = false)
    private CoverageDefinition coverage;

    @Column(name = "is_mandatory", nullable = false)
    @Builder.Default
    private boolean mandatory = false;

    @Column(name = "is_default_selected", nullable = false)
    @Builder.Default
    private boolean defaultSelected = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 1;
}

