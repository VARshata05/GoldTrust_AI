package com.goldtrust.jeweller;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "jewellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jeweller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "jeweller_code", nullable = false, unique = true, length = 50)
    private String jewellerCode;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(name = "license_number", nullable = false, length = 100)
    private String licenseNumber;

    @Column(name = "bis_hallmark_center_id", length = 100)
    private String bisHallmarkCenterId;

    @Column(name = "is_certified", nullable = false)
    @Builder.Default
    private boolean certified = true;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(length = 30)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(4.80);

    @Column(name = "verification_status", length = 50)
    @Builder.Default
    private String verificationStatus = "ACTIVE";

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();
}

