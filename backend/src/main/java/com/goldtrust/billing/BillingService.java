package com.goldtrust.billing;

import com.goldtrust.common.BadRequestException;
import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.policy.InsurancePolicy;
import com.goldtrust.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingAccountRepository accountRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public BillingAccount getOrCreateBillingAccount(Customer customer) {
        return accountRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    String accNum = "GT-BIL-2026-" + String.format("%06d", customer.getId());
                    BillingAccount acc = BillingAccount.builder()
                            .accountNumber(accNum)
                            .customer(customer)
                            .currentBalance(BigDecimal.ZERO)
                            .totalBilled(BigDecimal.ZERO)
                            .totalPaid(BigDecimal.ZERO)
                            .status("CURRENT")
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return accountRepository.save(acc);
                });
    }

    @Transactional
    public Invoice createInitialPolicyInvoice(InsurancePolicy policy) {
        BillingAccount account = getOrCreateBillingAccount(policy.getCustomer());

        long count = invoiceRepository.count() + 1;
        String invNum = String.format("GT-INV-2026-%06d", count);

        BigDecimal premium = policy.getAnnualPremium();
        BigDecimal tax = premium.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP); // 18% GST standard
        BigDecimal total = premium.add(tax);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invNum)
                .billingAccount(account)
                .policy(policy)
                .invoiceType("NEW_POLICY")
                .amount(premium)
                .taxAmount(tax)
                .totalAmount(total)
                .dueDate(LocalDate.now().plusDays(14))
                .status("PENDING")
                .notes("Annual premium for Gold Jewellery Policy " + policy.getPolicyNumber())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        // Update billing account totals
        account.setTotalBilled(account.getTotalBilled().add(total));
        account.setCurrentBalance(account.getCurrentBalance().add(total));
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);

        return saved;
    }

    @Transactional
    public BillingDTO.PaymentDTO processSimulatedPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + request.getInvoiceId()));

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            throw new BadRequestException("Invoice is already fully settled");
        }

        BillingAccount account = invoice.getBillingAccount();
        long payCount = paymentRepository.count() + 1;
        String payRef = String.format("GT-PAY-2026-%06d", payCount);

        Payment payment = Payment.builder()
                .paymentReference(payRef)
                .invoice(invoice)
                .billingAccount(account)
                .amountPaid(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .gatewayReference("SIM-GW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .transactionStatus("COMPLETED")
                .receiptUrl("/receipts/" + payRef + ".pdf")
                .paymentDate(Instant.now())
                .notes("Simulated safe academic payment transaction")
                .build();

        Payment saved = paymentRepository.save(payment);

        invoice.setStatus("PAID");
        invoice.setUpdatedAt(Instant.now());
        invoiceRepository.save(invoice);

        // Update billing balances
        account.setTotalPaid(account.getTotalPaid().add(request.getAmount()));
        BigDecimal newBalance = account.getCurrentBalance().subtract(request.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            newBalance = BigDecimal.ZERO;
        }
        account.setCurrentBalance(newBalance);
        account.setStatus("CURRENT");
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);

        return BillingDTO.PaymentDTO.builder()
                .id(saved.getId())
                .paymentReference(saved.getPaymentReference())
                .amountPaid(saved.getAmountPaid())
                .paymentMethod(saved.getPaymentMethod())
                .transactionStatus(saved.getTransactionStatus())
                .receiptUrl(saved.getReceiptUrl())
                .paymentDate(saved.getPaymentDate())
                .build();
    }

    @Transactional(readOnly = true)
    public BillingDTO getCurrentCustomerBilling() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        BillingAccount account = getOrCreateBillingAccount(customer);
        return mapToDTO(account);
    }

    private BillingDTO mapToDTO(BillingAccount account) {
        List<BillingDTO.InvoiceDTO> invoiceDTOs = invoiceRepository.findByBillingAccountIdOrderByCreatedAtDesc(account.getId()).stream()
                .map(inv -> {
                    List<BillingDTO.PaymentDTO> payments = paymentRepository.findByInvoiceId(inv.getId()).stream()
                            .map(p -> BillingDTO.PaymentDTO.builder()
                                    .id(p.getId())
                                    .paymentReference(p.getPaymentReference())
                                    .amountPaid(p.getAmountPaid())
                                    .paymentMethod(p.getPaymentMethod())
                                    .transactionStatus(p.getTransactionStatus())
                                    .receiptUrl(p.getReceiptUrl())
                                    .paymentDate(p.getPaymentDate())
                                    .build())
                            .collect(Collectors.toList());

                    return BillingDTO.InvoiceDTO.builder()
                            .id(inv.getId())
                            .invoiceNumber(inv.getInvoiceNumber())
                            .policyId(inv.getPolicy() != null ? inv.getPolicy().getId() : null)
                            .policyNumber(inv.getPolicy() != null ? inv.getPolicy().getPolicyNumber() : null)
                            .invoiceType(inv.getInvoiceType())
                            .amount(inv.getAmount())
                            .taxAmount(inv.getTaxAmount())
                            .totalAmount(inv.getTotalAmount())
                            .dueDate(inv.getDueDate())
                            .status(inv.getStatus())
                            .notes(inv.getNotes())
                            .createdAt(inv.getCreatedAt())
                            .payments(payments)
                            .build();
                }).collect(Collectors.toList());

        return BillingDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomer().getId())
                .customerName(account.getCustomer().getUser().getFirstName() + " " + account.getCustomer().getUser().getLastName())
                .currentBalance(account.getCurrentBalance())
                .totalBilled(account.getTotalBilled())
                .totalPaid(account.getTotalPaid())
                .status(account.getStatus())
                .invoices(invoiceDTOs)
                .build();
    }
}

