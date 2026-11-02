package com.goldtrust.billing;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Tag(name = "Billing & Payments", description = "Guidewire-aligned billing accounts, invoice schedules, and simulated cashless payments")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/accounts/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get current customer billing account balance and invoices")
    public ResponseEntity<ApiResponse<BillingDTO>> getMyBillingAccount() {
        return ResponseEntity.ok(ApiResponse.ok(billingService.getCurrentCustomerBilling()));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Process simulated premium payment")
    public ResponseEntity<ApiResponse<BillingDTO.PaymentDTO>> processPayment(@Valid @RequestBody PaymentRequest request) {
        BillingDTO.PaymentDTO payment = billingService.processSimulatedPayment(request);
        return ResponseEntity.ok(ApiResponse.ok(payment, "Payment completed successfully"));
    }
}

