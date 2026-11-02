package com.goldtrust.claim;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "claim_decisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Column(nullable = false, length = 50)
    private String decision; // APPROVED, REJECTED, REQUEST_INFORMATION

    @Column(name = "decision_rationale", nullable = false, columnDefinition = "TEXT")
    private String decisionRationale;

    @Column(name = "settlement_method", length = 50)
    private String settlementMethod; // REPAIR, REPLACEMENT, CASH

    @Column(name = "final_settlement_amount", precision = 15, scale = 2)
    private BigDecimal finalSettlementAmount;

    @Column(name = "decision_date")
    @Builder.Default
    private Instant decisionDate = Instant.now();
}

