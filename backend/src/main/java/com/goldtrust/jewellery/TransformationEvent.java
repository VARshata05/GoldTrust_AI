package com.goldtrust.jewellery;

import com.goldtrust.jeweller.Jeweller;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transformation_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransformationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @Column(name = "transformation_type", nullable = false, length = 50)
    private String transformationType; // PURCHASE, REGISTRATION, VERIFICATION, REPAIR, RESIZING, REDESIGN, WEIGHT_MODIFICATION, STONE_REPLACEMENT, COMPONENT_REPLACEMENT, OWNERSHIP_TRANSFER, INHERITANCE, APPRAISAL, INSURANCE_UPDATE

    @Column(name = "previous_version", nullable = false)
    private Integer previousVersion;

    @Column(name = "new_version", nullable = false)
    private Integer newVersion;

    @Column(name = "previous_weight", precision = 10, scale = 3)
    private BigDecimal previousWeight;

    @Column(name = "new_weight", precision = 10, scale = 3)
    private BigDecimal newWeight;

    @Column(name = "previous_purity", length = 20)
    private String previousPurity;

    @Column(name = "new_purity", length = 20)
    private String newPurity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jeweller_id")
    private Jeweller jeweller;

    @Column(name = "verification_status", length = 50)
    @Builder.Default
    private String verificationStatus = "VERIFIED";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "evidence_document_url", length = 500)
    private String evidenceDocumentUrl;

    @Column(name = "evidence_image_urls", columnDefinition = "TEXT")
    private String evidenceImageUrls;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

