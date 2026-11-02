package com.goldtrust.product;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Insurance Products & Coverages", description = "Endpoints for viewing configurable insurance plans and coverages")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all active insurance products with configurable coverages")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getActiveProducts() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getActiveProducts()));
    }

    @GetMapping("/{productCode}")
    @Operation(summary = "Get product details by code")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductByCode(@PathVariable String productCode) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductByCode(productCode)));
    }

    @GetMapping("/coverages")
    @Operation(summary = "Get all configurable coverages (the 9 gold insurance coverages)")
    public ResponseEntity<ApiResponse<List<CoverageDTO>>> getAllCoverages() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAllCoverages()));
    }
}

