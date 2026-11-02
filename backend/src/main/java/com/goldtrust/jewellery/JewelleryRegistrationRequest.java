package com.goldtrust.jewellery;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JewelleryRegistrationRequest {
    @NotBlank(message = "Jewellery name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category; // Ring, Necklace, Chain, Bracelet, Earrings, Bangle, Pendant, Watch, Other

    @NotBlank(message = "Purity is required (e.g., 22K, 24K, 18K)")
    private String purity;

    @NotNull(message = "Net gold weight is required")
    @DecimalMin(value = "0.01", message = "Net weight must be greater than 0")
    private BigDecimal netGoldWeight;

    @NotNull(message = "Gross weight is required")
    @DecimalMin(value = "0.01", message = "Gross weight must be greater than 0")
    private BigDecimal grossWeight;

    private String huid; // 6-digit Hallmark Unique Identification
    private String hallmarkStatus;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private BigDecimal appraisalValue;
    private String primaryImageUrl;
    private Long preferredJewellerId;
}

