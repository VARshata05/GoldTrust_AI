package com.goldtrust;

import com.goldtrust.claim.ClaimDTO;
import com.goldtrust.claim.ClaimDecisionRequest;
import com.goldtrust.claim.ClaimRepository;
import com.goldtrust.claim.ClaimService;
import com.goldtrust.claim.ClaimSubmissionRequest;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.jewellery.*;
import com.goldtrust.policy.InsurancePolicy;
import com.goldtrust.policy.InsurancePolicyRepository;
import com.goldtrust.product.InsuranceProduct;
import com.goldtrust.product.InsuranceProductRepository;
import com.goldtrust.quotation.QuotationEngineService;
import com.goldtrust.quotation.QuotationRequest;
import com.goldtrust.quotation.QuotationResponse;
import com.goldtrust.security.UserPrincipal;
import com.goldtrust.user.User;
import com.goldtrust.user.UserRepository;
import com.goldtrust.valuation.ValuationDTO;
import com.goldtrust.valuation.ValuationEngineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GoldTrustInsuranceLifecycleTests {

    @Autowired
    private QuotationEngineService quotationEngineService;

    @Autowired
    private ValuationEngineService valuationEngineService;

    @Autowired
    private JewelleryService jewelleryService;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JewelleryAssetRepository assetRepository;

    @Autowired
    private AssetVersionRepository versionRepository;

    @Autowired
    private InsuranceProductRepository productRepository;

    @Autowired
    private InsurancePolicyRepository policyRepository;

    @Autowired
    private ClaimRepository claimRepository;

    private void authenticateAs(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @Transactional
    @DisplayName("1. Quotation Engine: Calculates premium using formula with factors & deductible")
    void testQuotationEngineAndPremiumCalculation() {
        authenticateAs("customer");

        InsuranceProduct product = productRepository.findAll().stream()
                .filter(InsuranceProduct::isActive)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active product found"));

        JewelleryAsset asset = assetRepository.findByAssetId("GT-JWL-2026-000001")
                .orElseThrow(() -> new IllegalStateException("Seed asset GT-JWL-2026-000001 not found"));

        QuotationRequest req = QuotationRequest.builder()
                .productId(product.getId())
                .assetIds(List.of(asset.getAssetId()))
                .selectedCoverageCodes(List.of("COV_ACC_LOSS", "COV_THEFT", "COV_ACC_DAMAGE", "COV_REPAIR"))
                .deductiblePercentage(BigDecimal.valueOf(5.00))
                .stateOrRegion("Maharashtra")
                .build();

        QuotationResponse quote = quotationEngineService.generateQuotation(req);

        assertNotNull(quote);
        assertNotNull(quote.getQuotationNumber());
        assertTrue(quote.getQuotationNumber().startsWith("GT-QUO-2026-"));
        assertTrue(quote.getAnnualPremium().compareTo(BigDecimal.ZERO) > 0, "Annual premium must be > 0");
        assertTrue(quote.getMonthlyEquivalent().compareTo(BigDecimal.ZERO) > 0, "Monthly premium must be > 0");
        assertNotNull(quote.getCoverageFactor());
        assertNotNull(quote.getRiskFactor());
        assertNotNull(quote.getLocationFactor());
        assertNotNull(quote.getSelectedCoverages());
        assertFalse(quote.getSelectedCoverages().isEmpty());
        assertNotNull(quote.getItems());
        assertFalse(quote.getItems().isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("2. Dynamic Bullion Valuation: Computes gold value, stone value, making charges, and flags coverage gap")
    void testBullionValuationAndCoverageGapAlert() {
        // Evaluate valuation for asset GT-JWL-2026-000001
        ValuationDTO valuation = valuationEngineService.calculateValuation("GT-JWL-2026-000001", null);

        assertNotNull(valuation);
        assertEquals("GT-JWL-2026-000001", valuation.getAssetId());
        assertTrue(valuation.getGoldValue().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(valuation.getCurrentEstimatedValue().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(valuation.getApplicableGoldRate());

        // Test coverage gap detection when insured value is lower than current estimated value
        BigDecimal lowInsuredValue = BigDecimal.valueOf(50000.00);
        ValuationDTO gapValuation = valuationEngineService.calculateValuation("GT-JWL-2026-000001", lowInsuredValue);

        assertTrue(gapValuation.isGapIdentified(), "Should identify coverage gap when current value exceeds insured value");
        assertTrue(gapValuation.getCoverageGap().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(gapValuation.getGapPercentage().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(gapValuation.getRecommendation().contains("Valuation surge identified"));
    }

    @Test
    @Transactional
    @DisplayName("3. Transformation-Aware Lifecycle: Version increments, preserves immutable version history")
    void testImmutableJewelleryTransformationVersioning() {
        authenticateAs("customer");

        // 1. Customer registers a new jewellery item
        JewelleryRegistrationRequest regReq = JewelleryRegistrationRequest.builder()
                .name("Kundan Choker Necklace")
                .category("NECKLACE")
                .purity("22K")
                .netGoldWeight(BigDecimal.valueOf(42.50))
                .grossWeight(BigDecimal.valueOf(48.20))
                .huid("HUID883492")
                .hallmarkStatus("VERIFIED")
                .purchaseDate(LocalDate.now().minusMonths(3))
                .purchasePrice(BigDecimal.valueOf(320000.00))
                .build();

        JewelleryAssetDTO registeredAsset = jewelleryService.registerJewellery(regReq);
        assertNotNull(registeredAsset);
        assertEquals(1, registeredAsset.getCurrentVersion());
        String assetId = registeredAsset.getAssetId();

        // 2. Jeweller performs a transformation (Resizing / stone replacement)
        authenticateAs("jeweller");

        TransformationRequest transReq = TransformationRequest.builder()
                .transformationType("WEIGHT_MODIFICATION")
                .newWeight(BigDecimal.valueOf(44.00))
                .newGrossWeight(BigDecimal.valueOf(49.80))
                .newPurity("22K")
                .description("Customer requested additional 1.5g gold link reinforcement at verified jeweller workshop")
                .build();

        JewelleryAssetDTO updatedAsset = jewelleryService.recordTransformation(assetId, transReq);

        // 3. Verify version incremented to 2
        assertEquals(2, updatedAsset.getCurrentVersion());
        assertEquals(0, new BigDecimal("44.00").compareTo(updatedAsset.getNetGoldWeight()));

        // 4. Verify historical versions are preserved and never overwritten
        List<AssetVersion> versions = versionRepository.findByJewelleryAssetIdOrderByVersionNumberAsc(updatedAsset.getId());
        assertEquals(2, versions.size(), "Must contain exactly 2 immutable version records");

        AssetVersion v1 = versions.get(0);
        assertEquals(1, v1.getVersionNumber());
        assertEquals(0, new BigDecimal("42.50").compareTo(v1.getNetGoldWeight()), "Historical Version 1 must preserve original weight 42.50g");

        AssetVersion v2 = versions.get(1);
        assertEquals(2, v2.getVersionNumber());
        assertEquals(0, new BigDecimal("44.00").compareTo(v2.getNetGoldWeight()), "Version 2 must reflect transformed weight 44.00g");
    }

    @Test
    @Transactional
    @DisplayName("4. Claim Lifecycle: Submission, dynamic coverage check, reserve allocation, officer adjudication")
    void testClaimLifecycleAndAdjudication() {
        authenticateAs("customer");

        InsurancePolicy policy = policyRepository.findByPolicyNumber("GT-POL-2026-000001")
                .orElseThrow(() -> new IllegalStateException("Policy GT-POL-2026-000001 not found"));

        JewelleryAsset asset = assetRepository.findByAssetId("GT-JWL-2026-000001")
                .orElseThrow(() -> new IllegalStateException("Asset GT-JWL-2026-000001 not found"));

        ClaimSubmissionRequest subReq = ClaimSubmissionRequest.builder()
                .policyId(policy.getId())
                .assetId(asset.getAssetId())
                .incidentType("DAMAGE")
                .incidentDate(LocalDate.now().minusDays(2))
                .incidentLocation("Pune, Maharashtra")
                .incidentDescription("Accidental impact causing prong damage and loose setting")
                .claimedAmount(BigDecimal.valueOf(15000.00))
                .settlementPreference("REPAIR")
                .build();

        ClaimDTO submittedClaim = claimService.submitClaim(subReq);

        assertNotNull(submittedClaim);
        assertNotNull(submittedClaim.getClaimNumber());
        assertEquals("UNDER_REVIEW", submittedClaim.getStatus());
        assertEquals(BigDecimal.valueOf(15000.00), submittedClaim.getClaimedAmount());
        assertNotNull(submittedClaim.getAssessment());
        assertTrue(submittedClaim.getAssessment().isCoverageConfirmed(), "Coverage must be verified against actual policy coverages");

        // Insurance Officer reviews and adjudicates
        authenticateAs("officer");

        ClaimDecisionRequest decisionReq = ClaimDecisionRequest.builder()
                .decision("APPROVED")
                .decisionRationale("Loss incident verified against damage coverage terms. Approved network repair authorized.")
                .approvedAmount(BigDecimal.valueOf(14000.00))
                .settlementMethod("REPAIR")
                .build();

        ClaimDTO adjudicatedClaim = claimService.processDecision(submittedClaim.getId(), decisionReq);

        assertNotNull(adjudicatedClaim);
        assertEquals("APPROVED", adjudicatedClaim.getStatus());
        assertEquals(BigDecimal.valueOf(14000.00), adjudicatedClaim.getApprovedAmount());
        assertNotNull(adjudicatedClaim.getDeductibleAmount());
        assertTrue(adjudicatedClaim.getDeductibleAmount().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(adjudicatedClaim.getNetSettlementAmount());
    }
}

