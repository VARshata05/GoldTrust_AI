package com.goldtrust.jewellery;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "asset_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 20)
    private String purity;

    @Column(name = "net_gold_weight", nullable = false, precision = 10, scale = 3)
    private BigDecimal netGoldWeight;

    @Column(name = "gross_weight", nullable = false, precision = 10, scale = 3)
    private BigDecimal grossWeight;

    @Column(length = 50)
    private String huid;

    @Column(name = "hallmark_status", length = 50)
    private String hallmarkStatus;

    @Column(name = "estimated_value", precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "primary_image_url", length = 500)
    private String primaryImageUrl;

    @Column(name = "change_reason")
    private String changeReason;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

