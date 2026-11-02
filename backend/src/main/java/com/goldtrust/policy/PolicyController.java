package com.goldtrust.policy;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Tag(name = "Policy Administration", description = "Guidewire-aligned policy lifecycle, endorsements, and multi-asset coverage")
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    @Operation(summary = "Get policies (Customer sees own, Officer/Admin sees all)")
    public ResponseEntity<ApiResponse<List<PolicyDTO>>> getPolicies() {
        try {
            return ResponseEntity.ok(ApiResponse.ok(policyService.getCurrentCustomerPolicies()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok(policyService.getAllPolicies()));
        }
    }

    @GetMapping("/{policyNumber}")
    @Operation(summary = "Get policy details by policy number")
    public ResponseEntity<ApiResponse<PolicyDTO>> getPolicyByNumber(@PathVariable String policyNumber) {
        return ResponseEntity.ok(ApiResponse.ok(policyService.getPolicyByNumber(policyNumber)));
    }
}

