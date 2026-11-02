package com.goldtrust.underwriting;

import com.goldtrust.customer.Customer;
import com.goldtrust.product.InsuranceProduct;
import com.goldtrust.quotation.Quotation;
import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "insurance_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_number", nullable = false, unique = true, length = 50)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct product;

    @Column(name = "total_insured_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInsuredValue;

    @Column(name = "proposed_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal proposedPremium;

    @Column(length = 50)
    @Builder.Default
    private String status = "SUBMITTED"; // SUBMITTED, UNDERWRITING, APPROVED, REFERRED, DECLINED, REQUEST_INFORMATION

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "supporting_docs_json", columnDefinition = "TEXT")
    private String supportingDocsJson;

    @Column(name = "underwriting_decision", length = 50)
    private String underwritingDecision; // APPROVE, REFER, DECLINE, REQUEST_INFORMATION

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    @Column(name = "decision_date")
    private Instant decisionDate;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();
}

