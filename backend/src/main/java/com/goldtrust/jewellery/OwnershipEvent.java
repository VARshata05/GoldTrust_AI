package com.goldtrust.jewellery;

import com.goldtrust.customer.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "ownership_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnershipEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_customer_id")
    private Customer previousCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_customer_id", nullable = false)
    private Customer newCustomer;

    @Column(name = "transfer_type", nullable = false, length = 50)
    private String transferType; // PURCHASE, GIFT, INHERITANCE, PRIVATE_SALE

    @Column(name = "legal_document_ref", length = 100)
    private String legalDocumentRef;

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

