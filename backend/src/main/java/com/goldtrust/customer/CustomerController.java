package com.goldtrust.customer;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for customer accounts and profiles")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get current authenticated customer profile")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCurrentCustomer() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getCurrentCustomer()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSURANCE_OFFICER', 'ADMIN', 'JEWELLER')")
    @Operation(summary = "Get customer by ID (Officer/Admin/Jeweller)")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getCustomerById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "List all customers (Officer/Admin)")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAllCustomers()));
    }
}

