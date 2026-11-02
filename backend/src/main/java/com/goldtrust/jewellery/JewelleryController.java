package com.goldtrust.jewellery;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/jewellery")
@RequiredArgsConstructor
@Tag(name = "Jewellery Management", description = "Digital identity, asset versioning, and transformation lifecycle")
public class JewelleryController {

    private final JewelleryService jewelleryService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Register new jewellery asset with persistent digital identity")
    public ResponseEntity<ApiResponse<JewelleryAssetDTO>> registerJewellery(@Valid @RequestBody JewelleryRegistrationRequest request) {
        JewelleryAssetDTO created = jewelleryService.registerJewellery(request);
        return ResponseEntity.ok(ApiResponse.ok(created, "Jewellery registered successfully with digital identity " + created.getAssetId()));
    }

    @GetMapping
    @Operation(summary = "List jewellery assets (Customer sees own; Officer/Admin sees all)")
    public ResponseEntity<ApiResponse<List<JewelleryAssetDTO>>> getJewelleryList() {
        // If customer, return their assets; otherwise return all assets
        try {
            return ResponseEntity.ok(ApiResponse.ok(jewelleryService.getCurrentCustomerAssets()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok(jewelleryService.getAllAssets()));
        }
    }

    @GetMapping("/{assetId}")
    @Operation(summary = "Get jewellery asset details by asset ID")
    public ResponseEntity<ApiResponse<JewelleryAssetDTO>> getJewelleryByAssetId(@PathVariable String assetId) {
        return ResponseEntity.ok(ApiResponse.ok(jewelleryService.getAssetByAssetId(assetId)));
    }

    @GetMapping("/{assetId}/history")
    @Operation(summary = "Get full transformation and version history for jewellery asset")
    public ResponseEntity<ApiResponse<List<TransformationDTO>>> getJewelleryHistory(@PathVariable String assetId) {
        JewelleryAssetDTO asset = jewelleryService.getAssetByAssetId(assetId);
        return ResponseEntity.ok(ApiResponse.ok(asset.getTransformations()));
    }

    @PostMapping("/{assetId}/transformations")
    @PreAuthorize("hasAnyRole('JEWELLER', 'INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "Record physical transformation (repair, resizing, stone replacement) creating a new asset version")
    public ResponseEntity<ApiResponse<JewelleryAssetDTO>> recordTransformation(
            @PathVariable String assetId,
            @Valid @RequestBody TransformationRequest request) {
        JewelleryAssetDTO updated = jewelleryService.recordTransformation(assetId, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Transformation recorded. New asset version: v" + updated.getCurrentVersion()));
    }

    @PostMapping("/{assetId}/verify")
    @PreAuthorize("hasAnyRole('JEWELLER', 'INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "Verify jewellery physical specifications and hallmark")
    public ResponseEntity<ApiResponse<JewelleryAssetDTO>> verifyJewellery(
            @PathVariable String assetId,
            @RequestBody Map<String, Object> body) {
        boolean approved = body.containsKey("approved") ? (Boolean) body.get("approved") : true;
        String notes = (String) body.getOrDefault("notes", "Physical inspection verified successfully");
        JewelleryAssetDTO updated = jewelleryService.verifyJewellery(assetId, approved, notes);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Verification updated to " + updated.getVerificationStatus()));
    }

    @GetMapping("/pending-verification")
    @PreAuthorize("hasAnyRole('JEWELLER', 'INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "Get assets awaiting verification")
    public ResponseEntity<ApiResponse<List<JewelleryAssetDTO>>> getPendingVerification() {
        return ResponseEntity.ok(ApiResponse.ok(jewelleryService.getPendingVerificationAssets()));
    }
}

