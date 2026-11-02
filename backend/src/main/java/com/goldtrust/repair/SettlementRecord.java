package com.goldtrust.repair;

import com.goldtrust.claim.Claim;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "settlement_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settlement_reference", nullable = false, unique = true, length = 50)
    private String settlementReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Column(name = "settlement_type", nullable = false, length = 50)
    private String settlementType; // REPAIR_VENDOR_PAYMENT, REPLACEMENT_VENDOR_PAYMENT, CUSTOMER_CASH_SETTLEMENT

    @Column(name = "payee_type", nullable = false, length = 50)
    private String payeeType; // JEWELLER, CUSTOMER

    @Column(name = "payee_name", nullable = false, length = 150)
    private String payeeName;

    @Column(name = "settlement_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal settlementAmount;

    @Column(name = "simulated_payment_ref", length = 100)
    private String simulatedPaymentRef;

    @Column(length = 50)
    @Builder.Default
    private String status = "SETTLED";

    @Column(name = "settled_at")
    @Builder.Default
    private Instant settledAt = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String notes;
}

