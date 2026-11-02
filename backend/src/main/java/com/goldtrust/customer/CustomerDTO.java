package com.goldtrust.customer;

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
public class CustomerDTO {
    private Long id;
    private Long userId;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String kycStatus;
    private BigDecimal riskScore;
    private Instant createdAt;

    public static CustomerDTO fromEntity(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .userId(customer.getUser().getId())
                .customerNumber(customer.getCustomerNumber())
                .firstName(customer.getUser().getFirstName())
                .lastName(customer.getUser().getLastName())
                .email(customer.getUser().getEmail())
                .phone(customer.getUser().getPhoneNumber())
                .addressLine1(customer.getAddressLine1())
                .addressLine2(customer.getAddressLine2())
                .city(customer.getCity())
                .state(customer.getState())
                .postalCode(customer.getPostalCode())
                .country(customer.getCountry())
                .kycStatus(customer.getKycStatus())
                .riskScore(customer.getRiskScore())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}

