package com.goldtrust.policy;

import com.goldtrust.customer.Customer;
import com.goldtrust.product.InsuranceProduct;
import com.goldtrust.underwriting.InsuranceApplication;
import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insurance_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsurancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", nullable = false, unique = true, length = 50)
    private String policyNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private InsuranceApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct product;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "ISSUED"; 
    // DRAFT, QUOTED, APPLICATION_SUBMITTED, UNDERWRITING, APPROVED, ISSUED, ACTIVE, RENEWAL_PENDING, RENEWED, EXPIRED, CANCELLED

    @Column(name = "total_sum_insured", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSumInsured;

    @Column(name = "annual_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualPremium;

    @Column(name = "payment_frequency", length = 20)
    @Builder.Default
    private String paymentFrequency = "ANNUAL";

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "policy_document_url", length = 500)
    private String policyDocumentUrl;

    @Column(name = "renewal_count", nullable = false)
    @Builder.Default
    private Integer renewalCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_user_id")
    private User issuedByUser;

    @Column(name = "issued_at")
    @Builder.Default
    private Instant issuedAt = Instant.now();

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PolicyAsset> policyAssets = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PolicyCoverage> policyCoverages = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PolicyEndorsement> endorsements = new ArrayList<>();
}

