package com.goldtrust.claim;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "claim_financials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimFinancials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private Claim claim;

    @Column(name = "initial_reserve", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal initialReserve = BigDecimal.ZERO;

    @Column(name = "current_reserve", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal currentReserve = BigDecimal.ZERO;

    @Column(name = "incurred_total", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal incurredTotal = BigDecimal.ZERO;

    @Column(name = "paid_total", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal paidTotal = BigDecimal.ZERO;

    @Column(name = "salvage_recovery", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal salvageRecovery = BigDecimal.ZERO;

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();
}

