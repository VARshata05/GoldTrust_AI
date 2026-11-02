package com.goldtrust.billing;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_reference", nullable = false, unique = true, length = 50)
    private String paymentReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id", nullable = false)
    private BillingAccount billingAccount;

    @Column(name = "amount_paid", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // SIMULATED_UPI, SIMULATED_CARD, SIMULATED_NET_BANKING

    @Column(name = "gateway_reference", length = 100)
    private String gatewayReference;

    @Column(name = "transaction_status", nullable = false, length = 50)
    @Builder.Default
    private String transactionStatus = "COMPLETED"; // COMPLETED, FAILED, REFUNDED

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    @Column(name = "payment_date")
    @Builder.Default
    private Instant paymentDate = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String notes;
}

