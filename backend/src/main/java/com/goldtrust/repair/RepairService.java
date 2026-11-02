package com.goldtrust.repair;

import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jeweller.JewellerRepository;
import com.goldtrust.jewellery.JewelleryService;
import com.goldtrust.jewellery.TransformationRequest;
import com.goldtrust.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairOrderRepository repairOrderRepository;
    private final ReplacementOrderRepository replacementOrderRepository;
    private final JewellerRepository jewellerRepository;
    private final JewelleryService jewelleryService;

    @Transactional(readOnly = true)
    public List<RepairDTO> getJewellerRepairOrders() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jeweller jeweller = jewellerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Jeweller profile not found"));

        return repairOrderRepository.findByJewellerId(jeweller.getId()).stream()
                .map(RepairDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairDTO> getAllRepairOrders() {
        return repairOrderRepository.findAll().stream()
                .map(RepairDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public RepairDTO updateRepairStatus(Long orderId, String status, String notes, BigDecimal postRepairWeight, String postRepairImageUrl) {
        RepairOrder order = repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order not found: " + orderId));

        order.setStatus(status);
        if (notes != null) order.setJewellerNotes(notes);
        if (postRepairWeight != null) order.setPostRepairWeight(postRepairWeight);
        if (postRepairImageUrl != null) order.setPostRepairImageUrl(postRepairImageUrl);

        if ("COMPLETED".equalsIgnoreCase(status) || "VERIFIED".equalsIgnoreCase(status)) {
            order.setCompletedAt(Instant.now());

            // Record physical transformation creating a new immutable jewellery asset version
            BigDecimal newWeight = postRepairWeight != null ? postRepairWeight : order.getJewelleryAsset().getNetGoldWeight();
            TransformationRequest tr = TransformationRequest.builder()
                    .transformationType("REPAIR")
                    .newWeight(newWeight)
                    .description("Certified repair completion under claim " + order.getClaim().getClaimNumber() + ": " + (notes != null ? notes : "Restoration completed."))
                    .newImageUrl(postRepairImageUrl)
                    .build();
            jewelleryService.recordTransformation(order.getJewelleryAsset().getAssetId(), tr);
        }

        RepairOrder saved = repairOrderRepository.save(order);
        return RepairDTO.fromEntity(saved);
    }
}

