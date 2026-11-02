package com.goldtrust.claim;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Tag(name = "Claims Management", description = "End-to-end insurance claim adjudication lifecycle, evidence verification, and settlement")
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Submit new insurance claim with evidence")
    public ResponseEntity<ApiResponse<ClaimDTO>> submitClaim(@Valid @RequestBody ClaimSubmissionRequest request) {
        ClaimDTO claim = claimService.submitClaim(request);
        return ResponseEntity.ok(ApiResponse.ok(claim, "Claim registered successfully with number " + claim.getClaimNumber()));
    }

    @GetMapping
    @Operation(summary = "Get claims (Customer sees own, Officer/Admin sees all claims queue)")
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getClaims() {
        try {
            return ResponseEntity.ok(ApiResponse.ok(claimService.getCurrentCustomerClaims()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok(claimService.getAllClaims()));
        }
    }

    @GetMapping("/{claimNumber}")
    @Operation(summary = "Get claim details, evidence, and AI assessment by claim number")
    public ResponseEntity<ApiResponse<ClaimDTO>> getClaimByNumber(@PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.ok(claimService.getClaimByNumber(claimNumber)));
    }

    @PostMapping("/{claimId}/decision")
    @PreAuthorize("hasAnyRole('INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "Adjudicate claim: approve, reject, or request additional information")
    public ResponseEntity<ApiResponse<ClaimDTO>> processDecision(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimDecisionRequest request) {
        ClaimDTO updated = claimService.processDecision(claimId, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Claim decision recorded: " + request.getDecision()));
    }
}

