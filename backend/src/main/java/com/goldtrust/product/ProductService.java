package com.goldtrust.product;

import com.goldtrust.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final InsuranceProductRepository productRepository;
    private final CoverageDefinitionRepository coverageRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> getActiveProducts() {
        return productRepository.findByActiveTrue().stream().map(p -> {
            ProductDTO dto = ProductDTO.fromEntity(p);
            List<CoverageDTO> coverages = p.getProductCoverages().stream().map(pc -> {
                CoverageDTO covDto = CoverageDTO.fromEntity(pc.getCoverage());
                covDto.setMandatory(pc.isMandatory());
                covDto.setDefaultSelected(pc.isDefaultSelected());
                return covDto;
            }).collect(Collectors.toList());
            dto.setCoverages(coverages);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductByCode(String productCode) {
        InsuranceProduct p = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with code: " + productCode));
        ProductDTO dto = ProductDTO.fromEntity(p);
        List<CoverageDTO> coverages = p.getProductCoverages().stream().map(pc -> {
            CoverageDTO covDto = CoverageDTO.fromEntity(pc.getCoverage());
            covDto.setMandatory(pc.isMandatory());
            covDto.setDefaultSelected(pc.isDefaultSelected());
            return covDto;
        }).collect(Collectors.toList());
        dto.setCoverages(coverages);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CoverageDTO> getAllCoverages() {
        return coverageRepository.findByEnabledTrue().stream()
                .map(CoverageDTO::fromEntity)
                .collect(Collectors.toList());
    }
}

