package com.goldtrust.policy;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "policy_renewals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRenewal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @Column(name = "renewal_cycle", nullable = false)
    @Builder.Default
    private Integer renewalCycle = 1;

    @Column(name = "previous_sum_insured", nullable = false, precision = 15, scale = 2)
    private BigDecimal previousSumInsured;

    @Column(name = "new_sum_insured", nullable = false, precision = 15, scale = 2)
    private BigDecimal newSumInsured;

    @Column(name = "renewal_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal renewalPremium;

    @Column(name = "renewal_status", length = 50)
    @Builder.Default
    private String renewalStatus = "PENDING"; // PENDING, ACCEPTED, DECLINED, EXPIRED

    @Column(name = "renewal_effective_date", nullable = false)
    private LocalDate renewalEffectiveDate;

    @Column(name = "renewal_expiry_date", nullable = false)
    private LocalDate renewalExpiryDate;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

