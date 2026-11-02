package com.goldtrust.valuation;

import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.jewellery.JewelleryAsset;
import com.goldtrust.jewellery.JewelleryAssetRepository;
import com.goldtrust.workflow.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValuationEngineService {

    private final ValuationRecordRepository valuationRepository;
    private final GoldRateRepository goldRateRepository;
    private final JewelleryAssetRepository assetRepository;
    private final NotificationService notificationService;

    @Transactional
    public ValuationDTO calculateValuation(String assetId, BigDecimal insuredValueOverride) {
        JewelleryAsset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery asset not found: " + assetId));

        GoldRate rate = goldRateRepository.findFirstByActiveTrueOrderByEffectiveDateDesc()
                .orElse(GoldRate.builder()
                        .currency("INR")
                        .rate24k(BigDecimal.valueOf(7850))
                        .rate22k(BigDecimal.valueOf(7195))
                        .rate18k(BigDecimal.valueOf(5888))
                        .rate14k(BigDecimal.valueOf(4580))
                        .effectiveDate(LocalDate.now())
                        .active(true)
                        .build());

        BigDecimal ratePerGram = "24K".equalsIgnoreCase(asset.getPurity()) ? rate.getRate24k() :
                "18K".equalsIgnoreCase(asset.getPurity()) ? rate.getRate18k() :
                "14K".equalsIgnoreCase(asset.getPurity()) ? rate.getRate14k() : rate.getRate22k();

        BigDecimal goldValue = asset.getNetGoldWeight().multiply(ratePerGram).setScale(2, RoundingMode.HALF_UP);

        // Stone value (if gross > net weight, approximate stone contribution)
        BigDecimal stoneWeight = asset.getGrossWeight().subtract(asset.getNetGoldWeight());
        BigDecimal eligibleStoneValue = stoneWeight.compareTo(BigDecimal.ZERO) > 0 ?
                stoneWeight.multiply(BigDecimal.valueOf(1500)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // Making charges (standard benchmark ~10-12% of gold value)
        BigDecimal eligibleMakingCharges = goldValue.multiply(BigDecimal.valueOf(0.12)).setScale(2, RoundingMode.HALF_UP);

        // Total Jewellery Value = GoldValue + EligibleStoneValue + EligibleMakingCharges
        BigDecimal currentEstimatedValue = goldValue.add(eligibleStoneValue).add(eligibleMakingCharges);

        // Update asset's current estimated value
        asset.setCurrentEstimatedValue(currentEstimatedValue);
        assetRepository.save(asset);

        BigDecimal insuredVal = insuredValueOverride != null ? insuredValueOverride :
                (asset.getAppraisalValue() != null ? asset.getAppraisalValue() : asset.getPurchasePrice());

        BigDecimal coverageGap = BigDecimal.ZERO;
        BigDecimal gapPercentage = BigDecimal.ZERO;
        boolean gapIdentified = false;
        String recommendation = "Coverage is currently adequate.";

        if (insuredVal != null && currentEstimatedValue.compareTo(insuredVal) > 0) {
            coverageGap = currentEstimatedValue.subtract(insuredVal);
            gapPercentage = coverageGap.divide(insuredVal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            gapIdentified = true;
            recommendation = "Valuation surge identified! Current replacement value exceeds policy insured value by ₹" +
                    coverageGap + " (" + gapPercentage + "%). We recommend requesting an endorsement review to protect against underinsurance.";

            // Send notification to customer
            if (asset.getCustomer() != null && asset.getCustomer().getUser() != null) {
                notificationService.createNotification(
                        asset.getCustomer().getUser(),
                        "Coverage Gap Alert for " + asset.getName(),
                        "Your jewellery value increased to ₹" + currentEstimatedValue + ", exceeding your insured value of ₹" + insuredVal + " by ₹" + coverageGap + ". Consider updating your policy sum insured.",
                        "COVERAGE_GAP",
                        "/policies"
                );
            }
        }

        ValuationRecord record = ValuationRecord.builder()
                .jewelleryAsset(asset)
                .goldRate(rate)
                .netGoldWeight(asset.getNetGoldWeight())
                .purity(asset.getPurity())
                .applicableGoldRate(ratePerGram)
                .goldValue(goldValue)
                .eligibleStoneValue(eligibleStoneValue)
                .eligibleMakingCharges(eligibleMakingCharges)
                .currentEstimatedValue(currentEstimatedValue)
                .insuredValue(insuredVal != null ? insuredVal : BigDecimal.ZERO)
                .coverageGap(coverageGap)
                .gapPercentage(gapPercentage)
                .inflationAdjustmentRate(BigDecimal.valueOf(6.50)) // 6.5% gold inflation trend index
                .calculatedAt(Instant.now())
                .build();

        ValuationRecord saved = valuationRepository.save(record);

        return ValuationDTO.builder()
                .id(saved.getId())
                .assetId(asset.getAssetId())
                .assetName(asset.getName())
                .netGoldWeight(saved.getNetGoldWeight())
                .purity(saved.getPurity())
                .applicableGoldRate(saved.getApplicableGoldRate())
                .goldValue(saved.getGoldValue())
                .eligibleStoneValue(saved.getEligibleStoneValue())
                .eligibleMakingCharges(saved.getEligibleMakingCharges())
                .currentEstimatedValue(saved.getCurrentEstimatedValue())
                .insuredValue(saved.getInsuredValue())
                .coverageGap(saved.getCoverageGap())
                .gapPercentage(saved.getGapPercentage())
                .inflationAdjustmentRate(saved.getInflationAdjustmentRate())
                .gapIdentified(gapIdentified)
                .recommendation(recommendation)
                .calculatedAt(saved.getCalculatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ValuationDTO> getValuationHistory(String assetId) {
        JewelleryAsset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery asset not found: " + assetId));

        return valuationRepository.findByJewelleryAssetIdOrderByCalculatedAtDesc(asset.getId()).stream()
                .map(v -> ValuationDTO.builder()
                        .id(v.getId())
                        .assetId(asset.getAssetId())
                        .assetName(asset.getName())
                        .netGoldWeight(v.getNetGoldWeight())
                        .purity(v.getPurity())
                        .applicableGoldRate(v.getApplicableGoldRate())
                        .goldValue(v.getGoldValue())
                        .eligibleStoneValue(v.getEligibleStoneValue())
                        .eligibleMakingCharges(v.getEligibleMakingCharges())
                        .currentEstimatedValue(v.getCurrentEstimatedValue())
                        .insuredValue(v.getInsuredValue())
                        .coverageGap(v.getCoverageGap())
                        .gapPercentage(v.getGapPercentage())
                        .inflationAdjustmentRate(v.getInflationAdjustmentRate())
                        .gapIdentified(v.getCoverageGap().compareTo(BigDecimal.ZERO) > 0)
                        .recommendation(v.getCoverageGap().compareTo(BigDecimal.ZERO) > 0 ? "Coverage gap detected" : "Adequate")
                        .calculatedAt(v.getCalculatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GoldRate getLiveGoldRate() {
        return goldRateRepository.findFirstByActiveTrueOrderByEffectiveDateDesc()
                .orElse(GoldRate.builder()
                        .currency("INR")
                        .rate24k(BigDecimal.valueOf(7850))
                        .rate22k(BigDecimal.valueOf(7195))
                        .rate18k(BigDecimal.valueOf(5888))
                        .rate14k(BigDecimal.valueOf(4580))
                        .effectiveDate(LocalDate.now())
                        .active(true)
                        .build());
    }
}

