package com.goldtrust.jeweller;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jewellers")
@RequiredArgsConstructor
@Tag(name = "Jeweller Management", description = "Endpoints for certified jewellery verification centres and jewellers")
public class JewellerController {

    private final JewellerService jewellerService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('JEWELLER')")
    @Operation(summary = "Get current authenticated jeweller profile")
    public ResponseEntity<ApiResponse<JewellerDTO>> getCurrentJeweller() {
        return ResponseEntity.ok(ApiResponse.ok(jewellerService.getCurrentJeweller()));
    }

    @GetMapping
    @Operation(summary = "List all active certified jewellers")
    public ResponseEntity<ApiResponse<List<JewellerDTO>>> getAllJewellers() {
        return ResponseEntity.ok(ApiResponse.ok(jewellerService.getAllActiveJewellers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get jeweller details by ID")
    public ResponseEntity<ApiResponse<JewellerDTO>> getJewellerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(jewellerService.getJewellerById(id)));
    }
}

