package com.goldtrust.customer;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "customer_number", nullable = false, unique = true, length = 50)
    private String customerNumber;

    @Column(name = "id_type", length = 50)
    @Builder.Default
    private String idType = "NATIONAL_ID";

    @Column(name = "id_number", length = 100)
    private String idNumber;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 30)
    private String postalCode;

    @Column(length = 100)
    @Builder.Default
    private String country = "India";

    @Column(name = "kyc_status", length = 50)
    @Builder.Default
    private String kycStatus = "VERIFIED";

    @Column(name = "risk_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal riskScore = BigDecimal.valueOf(1.00);

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();
}

