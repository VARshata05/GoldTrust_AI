package com.goldtrust.policy;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "policy_endorsements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyEndorsement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "endorsement_number", nullable = false, unique = true, length = 50)
    private String endorsementNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @Column(name = "endorsement_type", nullable = false, length = 50)
    private String endorsementType; // VALUE_UPDATE, ASSET_ADDED, ASSET_REMOVED, COVERAGE_CHANGE, ADDRESS_CHANGE

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "previous_values_json", columnDefinition = "TEXT")
    private String previousValuesJson;

    @Column(name = "new_values_json", columnDefinition = "TEXT")
    private String newValuesJson;

    @Column(name = "premium_delta", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal premiumDelta = BigDecimal.ZERO;

    @Column(length = 50)
    @Builder.Default
    private String status = "APPROVED"; // REQUESTED, UNDER_REVIEW, APPROVED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedByUser;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

