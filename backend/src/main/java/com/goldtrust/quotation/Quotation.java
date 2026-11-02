package com.goldtrust.quotation;

import com.goldtrust.customer.Customer;
import com.goldtrust.product.InsuranceProduct;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quotation_number", nullable = false, unique = true, length = 50)
    private String quotationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct product;

    @Column(name = "insured_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuredValue;

    @Column(name = "deductible_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal deductiblePercentage = BigDecimal.valueOf(5.00);

    @Column(name = "base_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePremium;

    @Column(name = "risk_factor", nullable = false, precision = 6, scale = 4)
    @Builder.Default
    private BigDecimal riskFactor = BigDecimal.valueOf(1.0000);

    @Column(name = "coverage_factor", nullable = false, precision = 6, scale = 4)
    @Builder.Default
    private BigDecimal coverageFactor = BigDecimal.valueOf(1.0000);

    @Column(name = "location_factor", nullable = false, precision = 6, scale = 4)
    @Builder.Default
    private BigDecimal locationFactor = BigDecimal.valueOf(1.0000);

    @Column(name = "total_annual_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAnnualPremium;

    @Column(name = "monthly_equivalent", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyEquivalent;

    @Column(name = "selected_coverages_json", columnDefinition = "TEXT")
    private String selectedCoveragesJson;

    @Column(length = 50)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, CONVERTED_TO_APPLICATION, EXPIRED

    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuotationItem> items = new ArrayList<>();
}

