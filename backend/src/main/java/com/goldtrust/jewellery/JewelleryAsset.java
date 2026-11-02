package com.goldtrust.jewellery;

import com.goldtrust.customer.Customer;
import com.goldtrust.jeweller.Jeweller;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jewellery_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JewelleryAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_id", nullable = false, unique = true, length = 50)
    private String assetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 50)
    private String category; // Ring, Necklace, Chain, Bracelet, Earrings, Bangle, Pendant, Watch, Other

    @Column(nullable = false, length = 20)
    private String purity; // 24K, 22K, 18K, 14K

    @Column(name = "net_gold_weight", nullable = false, precision = 10, scale = 3)
    private BigDecimal netGoldWeight; // in grams

    @Column(name = "gross_weight", nullable = false, precision = 10, scale = 3)
    private BigDecimal grossWeight; // in grams

    @Column(length = 50)
    private String huid; // Hallmark Unique Identification (6-character alphanumeric)

    @Column(name = "hallmark_status", length = 50)
    @Builder.Default
    private String hallmarkStatus = "VERIFIED";

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_price", precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "appraisal_value", precision = 15, scale = 2)
    private BigDecimal appraisalValue;

    @Column(name = "current_estimated_value", precision = 15, scale = 2)
    private BigDecimal currentEstimatedValue;

    @Column(name = "primary_image_url", length = 500)
    private String primaryImageUrl;

    @Column(name = "ownership_status", length = 50)
    @Builder.Default
    private String ownershipStatus = "OWNED";

    @Column(name = "verification_status", length = 50)
    @Builder.Default
    private String verificationStatus = "PENDING_VERIFICATION"; // PENDING_VERIFICATION, VERIFIED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_jeweller_id")
    private Jeweller verifiedByJeweller;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "current_version", nullable = false)
    @Builder.Default
    private Integer currentVersion = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "registration_date")
    @Builder.Default
    private Instant registrationDate = Instant.now();

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "jewelleryAsset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AssetVersion> versions = new ArrayList<>();

    @OneToMany(mappedBy = "jewelleryAsset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TransformationEvent> transformations = new ArrayList<>();

    @OneToMany(mappedBy = "jewelleryAsset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<JewelleryImage> images = new ArrayList<>();
}

