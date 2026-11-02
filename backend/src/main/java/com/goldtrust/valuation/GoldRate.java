package com.goldtrust.valuation;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "gold_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "rate_24k", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate24k;

    @Column(name = "rate_22k", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate22k;

    @Column(name = "rate_18k", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate18k;

    @Column(name = "rate_14k", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate14k;

    @Column(length = 100)
    @Builder.Default
    private String source = "INDIA_BULLION_AND_JEWELLERS_ASSN";

    @Column(name = "effective_date", nullable = false)
    @Builder.Default
    private LocalDate effectiveDate = LocalDate.now();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

