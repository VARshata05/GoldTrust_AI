package com.goldtrust.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    List<Payment> findByBillingAccountIdOrderByPaymentDateDesc(Long billingAccountId);
    List<Payment> findByInvoiceId(Long invoiceId);
}

