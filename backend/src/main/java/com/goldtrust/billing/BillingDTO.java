package com.goldtrust.billing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDTO {
    private Long id;
    private String accountNumber;
    private Long customerId;
    private String customerName;
    private BigDecimal currentBalance;
    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private String status;
    private List<InvoiceDTO> invoices;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceDTO {
        private Long id;
        private String invoiceNumber;
        private Long policyId;
        private String policyNumber;
        private String invoiceType;
        private BigDecimal amount;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private LocalDate dueDate;
        private String status;
        private String notes;
        private Instant createdAt;
        private List<PaymentDTO> payments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDTO {
        private Long id;
        private String paymentReference;
        private BigDecimal amountPaid;
        private String paymentMethod;
        private String transactionStatus;
        private String receiptUrl;
        private Instant paymentDate;
    }
}

