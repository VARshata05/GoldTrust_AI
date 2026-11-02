package com.goldtrust.quotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldtrust.common.BadRequestException;
import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.jewellery.JewelleryAsset;
import com.goldtrust.jewellery.JewelleryAssetRepository;
import com.goldtrust.product.CoverageDefinition;
import com.goldtrust.product.CoverageDefinitionRepository;
import com.goldtrust.product.InsuranceProduct;
import com.goldtrust.product.InsuranceProductRepository;
import com.goldtrust.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationEngineService {

    private final QuotationRepository quotationRepository;
    private final InsuranceProductRepository productRepository;
    private final JewelleryAssetRepository assetRepository;
    private final CustomerRepository customerRepository;
    private final CoverageDefinitionRepository coverageRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public QuotationResponse generateQuotation(QuotationRequest request) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        InsuranceProduct product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance product not found with ID: " + request.getProductId()));

        List<JewelleryAsset> assets = new ArrayList<>();
        BigDecimal computedInsuredValue = BigDecimal.ZERO;

        for (String assetId : request.getAssetIds()) {
            JewelleryAsset asset = assetRepository.findByAssetId(assetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Jewellery asset not found: " + assetId));
            assets.add(asset);
            BigDecimal itemVal = asset.getCurrentEstimatedValue() != null ? asset.getCurrentEstimatedValue() :
                    (asset.getAppraisalValue() != null ? asset.getAppraisalValue() : asset.getPurchasePrice());
            computedInsuredValue = computedInsuredValue.add(itemVal);
        }

        BigDecimal totalInsuredValue = request.getCustomInsuredValue() != null &&
                request.getCustomInsuredValue().compareTo(BigDecimal.ZERO) > 0 ?
                request.getCustomInsuredValue() : computedInsuredValue;

        if (totalInsuredValue.compareTo(product.getMinSumInsured()) < 0) {
            throw new BadRequestException("Insured value is below minimum sum insured for this plan: ₹" + product.getMinSumInsured());
        }

        // Coverage factor calculation
        BigDecimal coverageFactor = BigDecimal.valueOf(1.0000);
        List<String> coveragesList = request.getSelectedCoverageCodes();
        if (coveragesList == null || coveragesList.isEmpty()) {
            coveragesList = List.of("COV_ACC_LOSS", "COV_THEFT", "COV_ACC_DAMAGE");
        }

        for (String code : coveragesList) {
            CoverageDefinition cov = coverageRepository.findByCoverageCode(code).orElse(null);
            if (cov != null) {
                coverageFactor = coverageFactor.add(cov.getPremiumFactor().multiply(BigDecimal.valueOf(10)));
            }
        }

        // Risk factor based on customer and verification
        BigDecimal riskFactor = customer.getRiskScore() != null ? customer.getRiskScore() : BigDecimal.valueOf(1.0000);
        boolean anyUnverified = assets.stream().anyMatch(a -> !"VERIFIED".equalsIgnoreCase(a.getVerificationStatus()));
        if (anyUnverified) {
            riskFactor = riskFactor.multiply(BigDecimal.valueOf(1.10)); // +10% risk if pending verification
        }

        // Location factor
        BigDecimal locationFactor = BigDecimal.valueOf(1.0000);
        if ("Maharashtra".equalsIgnoreCase(request.getStateOrRegion()) || "Delhi".equalsIgnoreCase(request.getStateOrRegion())) {
            locationFactor = BigDecimal.valueOf(1.0200); // Tier 1 metro actuarial index
        }

        // Actuarial formula: Premium = BaseRate * InsuredValue * RiskFactor * CoverageFactor * LocationFactor
        BigDecimal baseRate = product.getBaseRatePercentage();
        BigDecimal basePremium = totalInsuredValue.multiply(baseRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal annualPremium = basePremium
                .multiply(riskFactor)
                .multiply(coverageFactor)
                .multiply(locationFactor)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal monthlyEquivalent = annualPremium.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        BigDecimal deductiblePercentage = request.getDeductiblePercentage() != null ?
                request.getDeductiblePercentage() : BigDecimal.valueOf(5.00);

        long count = quotationRepository.count() + 1;
        String quotationNumber = String.format("GT-QUO-2026-%06d", count);

        String coveragesJson = "";
        try {
            coveragesJson = objectMapper.writeValueAsString(coveragesList);
        } catch (Exception ignored) {}

        Quotation quotation = Quotation.builder()
                .quotationNumber(quotationNumber)
                .customer(customer)
                .product(product)
                .insuredValue(totalInsuredValue)
                .deductiblePercentage(deductiblePercentage)
                .basePremium(basePremium)
                .riskFactor(riskFactor.setScale(4, RoundingMode.HALF_UP))
                .coverageFactor(coverageFactor.setScale(4, RoundingMode.HALF_UP))
                .locationFactor(locationFactor.setScale(4, RoundingMode.HALF_UP))
                .totalAnnualPremium(annualPremium)
                .monthlyEquivalent(monthlyEquivalent)
                .selectedCoveragesJson(coveragesJson)
                .status("ACTIVE")
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        List<QuotationItem> items = new ArrayList<>();
        List<QuotationResponse.QuotationItemDetail> itemDetails = new ArrayList<>();

        for (JewelleryAsset asset : assets) {
            BigDecimal itemVal = asset.getCurrentEstimatedValue() != null ? asset.getCurrentEstimatedValue() :
                    (asset.getAppraisalValue() != null ? asset.getAppraisalValue() : asset.getPurchasePrice());

            BigDecimal share = computedInsuredValue.compareTo(BigDecimal.ZERO) > 0 ?
                    itemVal.divide(computedInsuredValue, 4, RoundingMode.HALF_UP) : BigDecimal.ONE;
            BigDecimal allocated = annualPremium.multiply(share).setScale(2, RoundingMode.HALF_UP);

            QuotationItem qItem = QuotationItem.builder()
                    .quotation(quotation)
                    .jewelleryAsset(asset)
                    .insuredValue(itemVal)
                    .allocatedPremium(allocated)
                    .build();
            items.add(qItem);

            itemDetails.add(QuotationResponse.QuotationItemDetail.builder()
                    .assetId(asset.getAssetId())
                    .assetName(asset.getName())
                    .category(asset.getCategory())
                    .netGoldWeight(asset.getNetGoldWeight())
                    .insuredValue(itemVal)
                    .allocatedPremium(allocated)
                    .build());
        }

        quotation.setItems(items);
        Quotation saved = quotationRepository.save(quotation);

        return QuotationResponse.builder()
                .id(saved.getId())
                .quotationNumber(saved.getQuotationNumber())
                .productId(product.getId())
                .productName(product.getProductName())
                .totalInsuredValue(saved.getInsuredValue())
                .deductiblePercentage(saved.getDeductiblePercentage())
                .baseRate(baseRate)
                .basePremium(saved.getBasePremium())
                .riskFactor(saved.getRiskFactor())
                .coverageFactor(saved.getCoverageFactor())
                .locationFactor(saved.getLocationFactor())
                .annualPremium(saved.getTotalAnnualPremium())
                .monthlyEquivalent(saved.getMonthlyEquivalent())
                .selectedCoverages(coveragesList)
                .items(itemDetails)
                .validUntil(saved.getValidUntil())
                .disclaimer("Indicative actuarial quotation. Final acceptance and binding terms are subject to Underwriting Officer inspection and AI evidence verification.")
                .build();
    }

    @Transactional(readOnly = true)
    public QuotationResponse getQuotationByNumber(String quotationNumber) {
        Quotation q = quotationRepository.findByQuotationNumber(quotationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found: " + quotationNumber));

        List<String> coveragesList = List.of("COV_ACC_LOSS", "COV_THEFT", "COV_ACC_DAMAGE");
        try {
            if (q.getSelectedCoveragesJson() != null && !q.getSelectedCoveragesJson().isBlank()) {
                coveragesList = objectMapper.readValue(q.getSelectedCoveragesJson(), List.class);
            }
        } catch (Exception ignored) {}

        List<QuotationResponse.QuotationItemDetail> details = q.getItems().stream().map(item ->
                QuotationResponse.QuotationItemDetail.builder()
                        .assetId(item.getJewelleryAsset().getAssetId())
                        .assetName(item.getJewelleryAsset().getName())
                        .category(item.getJewelleryAsset().getCategory())
                        .netGoldWeight(item.getJewelleryAsset().getNetGoldWeight())
                        .insuredValue(item.getInsuredValue())
                        .allocatedPremium(item.getAllocatedPremium())
                        .build()
        ).collect(Collectors.toList());

        return QuotationResponse.builder()
                .id(q.getId())
                .quotationNumber(q.getQuotationNumber())
                .productId(q.getProduct().getId())
                .productName(q.getProduct().getProductName())
                .totalInsuredValue(q.getInsuredValue())
                .deductiblePercentage(q.getDeductiblePercentage())
                .baseRate(q.getProduct().getBaseRatePercentage())
                .basePremium(q.getBasePremium())
                .riskFactor(q.getRiskFactor())
                .coverageFactor(q.getCoverageFactor())
                .locationFactor(q.getLocationFactor())
                .annualPremium(q.getTotalAnnualPremium())
                .monthlyEquivalent(q.getMonthlyEquivalent())
                .selectedCoverages(coveragesList)
                .items(details)
                .validUntil(q.getValidUntil())
                .disclaimer("Indicative actuarial quotation. Final acceptance and binding terms are subject to Underwriting Officer inspection and AI evidence verification.")
                .build();
    }
}

