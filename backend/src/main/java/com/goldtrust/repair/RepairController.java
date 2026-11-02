package com.goldtrust.repair;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
@Tag(name = "Repair & Replacement", description = "Certified network repair orchestration and asset restitution")
public class RepairController {

    private final RepairService repairService;

    @GetMapping
    @PreAuthorize("hasAnyRole('JEWELLER', 'INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "Get repair orders (Jeweller sees assigned orders; Officer/Admin sees all)")
    public ResponseEntity<ApiResponse<List<RepairDTO>>> getRepairOrders() {
        try {
            return ResponseEntity.ok(ApiResponse.ok(repairService.getJewellerRepairOrders()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok(repairService.getAllRepairOrders()));
        }
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('JEWELLER', 'INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "Update repair order status, submit post-repair weight and photographic evidence")
    public ResponseEntity<ApiResponse<RepairDTO>> updateStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> body) {
        String status = (String) body.get("status");
        String notes = (String) body.get("notes");
        BigDecimal postRepairWeight = body.get("postRepairWeight") != null ?
                new BigDecimal(body.get("postRepairWeight").toString()) : null;
        String postRepairImageUrl = (String) body.get("postRepairImageUrl");

        RepairDTO updated = repairService.updateRepairStatus(orderId, status, notes, postRepairWeight, postRepairImageUrl);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Repair order status updated to " + status));
    }
}

