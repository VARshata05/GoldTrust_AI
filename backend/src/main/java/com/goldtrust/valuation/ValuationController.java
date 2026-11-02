package com.goldtrust.valuation;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/valuation")
@RequiredArgsConstructor
@Tag(name = "Dynamic Valuation", description = "Real-time bullion gold rates, dynamic jewellery valuation, inflation protection, and coverage gap detection")
public class ValuationController {

    private final ValuationEngineService valuationService;

    @GetMapping("/rates/live")
    @Operation(summary = "Get current active benchmark bullion gold rates per gram (24K, 22K, 18K, 14K)")
    public ResponseEntity<ApiResponse<GoldRate>> getLiveRates() {
        return ResponseEntity.ok(ApiResponse.ok(valuationService.getLiveGoldRate()));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Recalculate dynamic jewellery asset valuation against live gold rates")
    public ResponseEntity<ApiResponse<ValuationDTO>> calculateValuation(@RequestBody Map<String, Object> body) {
        String assetId = (String) body.get("assetId");
        BigDecimal insuredOverride = body.get("insuredValue") != null ?
                new BigDecimal(body.get("insuredValue").toString()) : null;
        ValuationDTO dto = valuationService.calculateValuation(assetId, insuredOverride);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Dynamic valuation calculated"));
    }

    @GetMapping("/{assetId}")
    @Operation(summary = "Get current valuation and coverage gap for asset")
    public ResponseEntity<ApiResponse<ValuationDTO>> getAssetValuation(@PathVariable String assetId) {
        ValuationDTO dto = valuationService.calculateValuation(assetId, null);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/{assetId}/history")
    @Operation(summary = "Get historical valuation records for asset")
    public ResponseEntity<ApiResponse<List<ValuationDTO>>> getValuationHistory(@PathVariable String assetId) {
        return ResponseEntity.ok(ApiResponse.ok(valuationService.getValuationHistory(assetId)));
    }
}

