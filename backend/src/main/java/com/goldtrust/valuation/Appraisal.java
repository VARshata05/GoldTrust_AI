package com.goldtrust.valuation;

import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jewellery.JewelleryAsset;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "appraisals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jeweller_id")
    private Jeweller jeweller;

    @Column(name = "appraiser_name", length = 150)
    private String appraiserName;

    @Column(name = "appraised_gold_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal appraisedGoldValue;

    @Column(name = "appraised_stones_value", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal appraisedStonesValue = BigDecimal.ZERO;

    @Column(name = "appraised_making_charges", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal appraisedMakingCharges = BigDecimal.ZERO;

    @Column(name = "total_appraisal_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAppraisalValue;

    @Column(name = "certificate_number", length = 100)
    private String certificateNumber;

    @Column(name = "appraisal_date", nullable = false)
    private LocalDate appraisalDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

