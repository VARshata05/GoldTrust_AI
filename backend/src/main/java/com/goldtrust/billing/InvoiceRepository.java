package com.goldtrust.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByBillingAccountIdOrderByCreatedAtDesc(Long billingAccountId);
    List<Invoice> findByPolicyId(Long policyId);
    List<Invoice> findByStatus(String status);
}

