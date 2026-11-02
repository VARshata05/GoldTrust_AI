package com.goldtrust.jewellery;

import com.goldtrust.common.BadRequestException;
import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jeweller.JewellerRepository;
import com.goldtrust.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JewelleryService {

    private final JewelleryAssetRepository assetRepository;
    private final AssetVersionRepository versionRepository;
    private final JewelleryImageRepository imageRepository;
    private final TransformationEventRepository transformationRepository;
    private final CustomerRepository customerRepository;
    private final JewellerRepository jewellerRepository;

    @Transactional
    public JewelleryAssetDTO registerJewellery(JewelleryRegistrationRequest request) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user: " + principal.getUsername()));

        // Generate enterprise asset identifier: GT-JWL-2026-XXXXXX
        long count = assetRepository.count() + 1;
        String assetId = String.format("GT-JWL-2026-%06d", count);

        // Benchmark gold valuation estimation (~₹7,195/g for 22K)
        BigDecimal ratePerGram = "24K".equalsIgnoreCase(request.getPurity()) ? BigDecimal.valueOf(7850) :
                "18K".equalsIgnoreCase(request.getPurity()) ? BigDecimal.valueOf(5888) :
                "14K".equalsIgnoreCase(request.getPurity()) ? BigDecimal.valueOf(4580) : BigDecimal.valueOf(7195);
        BigDecimal estimatedGoldValue = request.getNetGoldWeight().multiply(ratePerGram);

        Jeweller preferredJeweller = null;
        if (request.getPreferredJewellerId() != null) {
            preferredJeweller = jewellerRepository.findById(request.getPreferredJewellerId()).orElse(null);
        }

        JewelleryAsset asset = JewelleryAsset.builder()
                .assetId(assetId)
                .customer(customer)
                .name(request.getName())
                .category(request.getCategory())
                .purity(request.getPurity().toUpperCase())
                .netGoldWeight(request.getNetGoldWeight())
                .grossWeight(request.getGrossWeight())
                .huid(request.getHuid() != null && !request.getHuid().isBlank() ? request.getHuid().toUpperCase() : "HUID-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .hallmarkStatus(request.getHallmarkStatus() != null ? request.getHallmarkStatus() : "VERIFIED")
                .purchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDate.now().minusMonths(6))
                .purchasePrice(request.getPurchasePrice() != null ? request.getPurchasePrice() : estimatedGoldValue.multiply(BigDecimal.valueOf(1.15)))
                .appraisalValue(request.getAppraisalValue() != null ? request.getAppraisalValue() : estimatedGoldValue.multiply(BigDecimal.valueOf(1.10)))
                .currentEstimatedValue(estimatedGoldValue.multiply(BigDecimal.valueOf(1.12)))
                .primaryImageUrl(request.getPrimaryImageUrl() != null ? request.getPrimaryImageUrl() : "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=600&q=80")
                .ownershipStatus("OWNED")
                .verificationStatus(preferredJeweller != null ? "PENDING_VERIFICATION" : "VERIFIED")
                .verifiedByJeweller(preferredJeweller)
                .currentVersion(1)
                .active(true)
                .registrationDate(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        JewelleryAsset savedAsset = assetRepository.save(asset);

        // Create Version 1 snapshot
        AssetVersion v1 = AssetVersion.builder()
                .jewelleryAsset(savedAsset)
                .versionNumber(1)
                .name(savedAsset.getName())
                .category(savedAsset.getCategory())
                .purity(savedAsset.getPurity())
                .netGoldWeight(savedAsset.getNetGoldWeight())
                .grossWeight(savedAsset.getGrossWeight())
                .huid(savedAsset.getHuid())
                .hallmarkStatus(savedAsset.getHallmarkStatus())
                .estimatedValue(savedAsset.getCurrentEstimatedValue())
                .primaryImageUrl(savedAsset.getPrimaryImageUrl())
                .changeReason("Initial Asset Registration & Baseline Creation")
                .createdBy(principal.getUsername())
                .createdAt(Instant.now())
                .build();
        versionRepository.save(v1);

        // Record Initial Registration Lifecycle Transformation Event
        TransformationEvent regEvent = TransformationEvent.builder()
                .jewelleryAsset(savedAsset)
                .transformationType("REGISTRATION")
                .previousVersion(1)
                .newVersion(1)
                .previousWeight(savedAsset.getNetGoldWeight())
                .newWeight(savedAsset.getNetGoldWeight())
                .previousPurity(savedAsset.getPurity())
                .newPurity(savedAsset.getPurity())
                .jeweller(preferredJeweller)
                .verificationStatus(savedAsset.getVerificationStatus())
                .description("Customer initiated digital identity creation with BIS hallmark verification.")
                .createdBy(principal.getUsername())
                .createdAt(Instant.now())
                .build();
        transformationRepository.save(regEvent);

        // Save Primary Image
        JewelleryImage img = JewelleryImage.builder()
                .jewelleryAsset(savedAsset)
                .versionNumber(1)
                .imageType("PRIMARY")
                .filePath("uploads/" + assetId + "_v1.jpg")
                .fileUrl(savedAsset.getPrimaryImageUrl())
                .originalFilename("asset_primary.jpg")
                .aiFeaturesExtracted(true)
                .createdAt(Instant.now())
                .build();
        imageRepository.save(img);

        return getAssetByAssetId(savedAsset.getAssetId());
    }

    @Transactional(readOnly = true)
    public JewelleryAssetDTO getAssetByAssetId(String assetId) {
        JewelleryAsset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery asset not found with assetId: " + assetId));

        JewelleryAssetDTO dto = JewelleryAssetDTO.fromEntity(asset);

        List<AssetVersionDTO> versions = versionRepository.findByJewelleryAssetIdOrderByVersionNumberAsc(asset.getId())
                .stream().map(AssetVersionDTO::fromEntity).collect(Collectors.toList());
        dto.setVersions(versions);

        List<TransformationDTO> transformations = transformationRepository.findByJewelleryAssetIdOrderByCreatedAtAsc(asset.getId())
                .stream().map(TransformationDTO::fromEntity).collect(Collectors.toList());
        dto.setTransformations(transformations);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<JewelleryAssetDTO> getCustomerAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId).stream()
                .map(JewelleryAssetDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JewelleryAssetDTO> getCurrentCustomerAssets() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
        return getCustomerAssets(customer.getId());
    }

    @Transactional(readOnly = true)
    public List<JewelleryAssetDTO> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(JewelleryAssetDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JewelleryAssetDTO> getPendingVerificationAssets() {
        return assetRepository.findByVerificationStatus("PENDING_VERIFICATION").stream()
                .map(JewelleryAssetDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public JewelleryAssetDTO recordTransformation(String assetId, TransformationRequest request) {
        JewelleryAsset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery asset not found: " + assetId));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jeweller jeweller = jewellerRepository.findByUserId(principal.getId()).orElse(null);

        int prevVersionNum = asset.getCurrentVersion();
        int newVersionNum = prevVersionNum + 1;

        BigDecimal prevWeight = asset.getNetGoldWeight();
        String prevPurity = asset.getPurity();

        // Update asset with new transformation parameters
        asset.setCurrentVersion(newVersionNum);
        asset.setNetGoldWeight(request.getNewWeight());
        if (request.getNewGrossWeight() != null) {
            asset.setGrossWeight(request.getNewGrossWeight());
        }
        if (request.getNewPurity() != null && !request.getNewPurity().isBlank()) {
            asset.setPurity(request.getNewPurity().toUpperCase());
        }
        if (request.getNewImageUrl() != null && !request.getNewImageUrl().isBlank()) {
            asset.setPrimaryImageUrl(request.getNewImageUrl());
        }
        asset.setUpdatedAt(Instant.now());

        // Re-estimate value with updated weight & purity
        BigDecimal ratePerGram = "24K".equalsIgnoreCase(asset.getPurity()) ? BigDecimal.valueOf(7850) :
                "18K".equalsIgnoreCase(asset.getPurity()) ? BigDecimal.valueOf(5888) :
                "14K".equalsIgnoreCase(asset.getPurity()) ? BigDecimal.valueOf(4580) : BigDecimal.valueOf(7195);
        BigDecimal updatedGoldValue = asset.getNetGoldWeight().multiply(ratePerGram).multiply(BigDecimal.valueOf(1.12));
        asset.setCurrentEstimatedValue(updatedGoldValue);

        JewelleryAsset savedAsset = assetRepository.save(asset);

        // Create new AssetVersion record (Preserving history: never overwrite)
        AssetVersion newVersion = AssetVersion.builder()
                .jewelleryAsset(savedAsset)
                .versionNumber(newVersionNum)
                .name(savedAsset.getName())
                .category(savedAsset.getCategory())
                .purity(savedAsset.getPurity())
                .netGoldWeight(savedAsset.getNetGoldWeight())
                .grossWeight(savedAsset.getGrossWeight())
                .huid(savedAsset.getHuid())
                .hallmarkStatus(savedAsset.getHallmarkStatus())
                .estimatedValue(savedAsset.getCurrentEstimatedValue())
                .primaryImageUrl(savedAsset.getPrimaryImageUrl())
                .changeReason(request.getDescription() != null ? request.getDescription() : "Transformation: " + request.getTransformationType())
                .createdBy(principal.getUsername())
                .createdAt(Instant.now())
                .build();
        versionRepository.save(newVersion);

        // Record Transformation Event
        TransformationEvent event = TransformationEvent.builder()
                .jewelleryAsset(savedAsset)
                .transformationType(request.getTransformationType())
                .previousVersion(prevVersionNum)
                .newVersion(newVersionNum)
                .previousWeight(prevWeight)
                .newWeight(request.getNewWeight())
                .previousPurity(prevPurity)
                .newPurity(savedAsset.getPurity())
                .jeweller(jeweller)
                .verificationStatus("VERIFIED")
                .description(request.getDescription())
                .evidenceDocumentUrl(request.getEvidenceDocumentUrl())
                .createdBy(principal.getUsername())
                .createdAt(Instant.now())
                .build();
        transformationRepository.save(event);

        return getAssetByAssetId(savedAsset.getAssetId());
    }

    @Transactional
    public JewelleryAssetDTO verifyJewellery(String assetId, boolean approved, String notes) {
        JewelleryAsset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery asset not found: " + assetId));

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jeweller jeweller = jewellerRepository.findByUserId(principal.getId()).orElse(null);

        asset.setVerificationStatus(approved ? "VERIFIED" : "REJECTED");
        asset.setVerifiedByJeweller(jeweller);
        asset.setVerifiedAt(Instant.now());
        asset.setUpdatedAt(Instant.now());

        JewelleryAsset saved = assetRepository.save(asset);

        TransformationEvent verEvent = TransformationEvent.builder()
                .jewelleryAsset(saved)
                .transformationType("VERIFICATION")
                .previousVersion(saved.getCurrentVersion())
                .newVersion(saved.getCurrentVersion())
                .previousWeight(saved.getNetGoldWeight())
                .newWeight(saved.getNetGoldWeight())
                .previousPurity(saved.getPurity())
                .newPurity(saved.getPurity())
                .jeweller(jeweller)
                .verificationStatus(saved.getVerificationStatus())
                .description("Jeweller in-person verification: " + (notes != null ? notes : "Weight, purity, and HUID confirmed."))
                .createdBy(principal.getUsername())
                .createdAt(Instant.now())
                .build();
        transformationRepository.save(verEvent);

        return getAssetByAssetId(saved.getAssetId());
    }
}

