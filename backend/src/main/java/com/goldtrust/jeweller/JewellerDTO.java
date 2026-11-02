package com.goldtrust.jeweller;

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
public class JewellerDTO {
    private Long id;
    private Long userId;
    private String jewellerCode;
    private String businessName;
    private String licenseNumber;
    private String bisHallmarkCenterId;
    private boolean certified;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    private BigDecimal rating;
    private String verificationStatus;
    private Instant createdAt;

    public static JewellerDTO fromEntity(Jeweller jeweller) {
        return JewellerDTO.builder()
                .id(jeweller.getId())
                .userId(jeweller.getUser().getId())
                .jewellerCode(jeweller.getJewellerCode())
                .businessName(jeweller.getBusinessName())
                .licenseNumber(jeweller.getLicenseNumber())
                .bisHallmarkCenterId(jeweller.getBisHallmarkCenterId())
                .certified(jeweller.isCertified())
                .contactPerson(jeweller.getContactPerson())
                .phone(jeweller.getPhone())
                .email(jeweller.getEmail())
                .address(jeweller.getAddress())
                .city(jeweller.getCity())
                .state(jeweller.getState())
                .rating(jeweller.getRating())
                .verificationStatus(jeweller.getVerificationStatus())
                .createdAt(jeweller.getCreatedAt())
                .build();
    }
}

