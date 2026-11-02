package com.goldtrust.jewellery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformationDTO {
    private Long id;
    private String transformationType;
    private Integer previousVersion;
    private Integer newVersion;
    private BigDecimal previousWeight;
    private BigDecimal newWeight;
    private String previousPurity;
    private String newPurity;
    private Long jewellerId;
    private String jewellerName;
    private String verificationStatus;
    private String description;
    private String evidenceDocumentUrl;
    private String evidenceImageUrls;
    private String createdBy;
    private Instant createdAt;

    public static TransformationDTO fromEntity(TransformationEvent event) {
        return TransformationDTO.builder()
                .id(event.getId())
                .transformationType(event.getTransformationType())
                .previousVersion(event.getPreviousVersion())
                .newVersion(event.getNewVersion())
                .previousWeight(event.getPreviousWeight())
                .newWeight(event.getNewWeight())
                .previousPurity(event.getPreviousPurity())
                .newPurity(event.getNewPurity())
                .jewellerId(event.getJeweller() != null ? event.getJeweller().getId() : null)
                .jewellerName(event.getJeweller() != null ? event.getJeweller().getBusinessName() : null)
                .verificationStatus(event.getVerificationStatus())
                .description(event.getDescription())
                .evidenceDocumentUrl(event.getEvidenceDocumentUrl())
                .evidenceImageUrls(event.getEvidenceImageUrls())
                .createdBy(event.getCreatedBy())
                .createdAt(event.getCreatedAt())
                .build();
    }
}

