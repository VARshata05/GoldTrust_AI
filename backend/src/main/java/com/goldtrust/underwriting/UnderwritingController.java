package com.goldtrust.underwriting;

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
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Tag(name = "Insurance Applications & Underwriting", description = "Guidewire-aligned underwriting decision engine and application processing")
public class UnderwritingController {

    private final UnderwritingService underwritingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Submit insurance application from a quotation")
    public ResponseEntity<ApiResponse<ApplicationDTO>> submitApplication(@RequestBody Map<String, Object> body) {
        Long quotationId = Long.valueOf(body.get("quotationId").toString());
        String customerNotes = (String) body.getOrDefault("customerNotes", "");
        ApplicationDTO dto = underwritingService.submitApplication(quotationId, customerNotes);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Application submitted for underwriting review"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "List all applications in underwriting queue")
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getApplicationsQueue() {
        return ResponseEntity.ok(ApiResponse.ok(underwritingService.getUnderwritingQueue()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application details and assessments by ID")
    public ResponseEntity<ApiResponse<ApplicationDTO>> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(underwritingService.getApplicationById(id)));
    }

    @PostMapping("/{id}/underwriting")
    @PreAuthorize("hasAnyRole('INSURANCE_OFFICER', 'ADMIN')")
    @Operation(summary = "Record underwriting decision (APPROVE, REFER, DECLINE, REQUEST_INFORMATION)")
    public ResponseEntity<ApiResponse<ApplicationDTO>> evaluateUnderwriting(
            @PathVariable Long id,
            @Valid @RequestBody UnderwritingDecisionRequest request) {
        ApplicationDTO dto = underwritingService.processDecision(id, request);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Underwriting decision applied: " + request.getDecision()));
    }
}

