package com.goldtrust.quotation;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quotations")
@RequiredArgsConstructor
@Tag(name = "Insurance Quotation", description = "Quotation engine with configurable risk and coverage factors")
public class QuotationController {

    private final QuotationEngineService quotationEngineService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Calculate and generate an insurance quotation")
    public ResponseEntity<ApiResponse<QuotationResponse>> generateQuotation(@Valid @RequestBody QuotationRequest request) {
        QuotationResponse response = quotationEngineService.generateQuotation(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Quotation generated successfully"));
    }

    @GetMapping("/{quotationNumber}")
    @Operation(summary = "Retrieve quotation details by number")
    public ResponseEntity<ApiResponse<QuotationResponse>> getQuotation(@PathVariable String quotationNumber) {
        return ResponseEntity.ok(ApiResponse.ok(quotationEngineService.getQuotationByNumber(quotationNumber)));
    }
}

