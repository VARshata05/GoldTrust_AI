package com.goldtrust.jewellery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformationRequest {
    @NotBlank(message = "Transformation type is required")
    private String transformationType; // REPAIR, RESIZING, REDESIGN, WEIGHT_MODIFICATION, STONE_REPLACEMENT, etc.

    @NotNull(message = "New net gold weight is required")
    private BigDecimal newWeight;

    private BigDecimal newGrossWeight;
    private String newPurity;
    private String description;
    private String evidenceDocumentUrl;
    private String newImageUrl;
}

