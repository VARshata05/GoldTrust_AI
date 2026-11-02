package com.goldtrust.policy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldtrust.billing.BillingService;
import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.product.CoverageDefinition;
import com.goldtrust.product.CoverageDefinitionRepository;
import com.goldtrust.quotation.Quotation;
import com.goldtrust.quotation.QuotationItem;
import com.goldtrust.security.UserPrincipal;
import com.goldtrust.underwriting.InsuranceApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final InsurancePolicyRepository policyRepository;
    private final CoverageDefinitionRepository coverageRepository;
    private final CustomerRepository customerRepository;
    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    public PolicyService(
            InsurancePolicyRepository policyRepository,
            CoverageDefinitionRepository coverageRepository,
            CustomerRepository customerRepository,
            @Lazy BillingService billingService,
            ObjectMapper objectMapper) {
        this.policyRepository = policyRepository;
        this.coverageRepository = coverageRepository;
        this.customerRepository = customerRepository;
        this.billingService = billingService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public InsurancePolicy issuePolicyFromApplication(InsuranceApplication app) {
        Quotation quotation = app.getQuotation();
        long count = policyRepository.count() + 1;
        String policyNum = String.format("GT-POL-2026-%06d", count);

        LocalDate startDate = LocalDate.now();
        LocalDate expiryDate = startDate.plusYears(1);

        InsurancePolicy policy = InsurancePolicy.builder()
                .policyNumber(policyNum)
                .application(app)
                .customer(app.getCustomer())
                .product(app.getProduct())
                .status("ACTIVE")
                .totalSumInsured(app.getTotalInsuredValue())
                .annualPremium(app.getProposedPremium())
                .paymentFrequency("ANNUAL")
                .effectiveDate(startDate)
                .expirationDate(expiryDate)
                .policyDocumentUrl("/documents/policies/" + policyNum + ".pdf")
                .renewalCount(0)
                .issuedByUser(app.getAssignedOfficer())
                .issuedAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        List<PolicyAsset> assets = new ArrayList<>();
        if (quotation != null && quotation.getItems() != null) {
            for (QuotationItem item : quotation.getItems()) {
                PolicyAsset pa = PolicyAsset.builder()
                        .policy(policy)
                        .jewelleryAsset(item.getJewelleryAsset())
                        .assetVersionNumber(item.getJewelleryAsset().getCurrentVersion())
                        .sumInsured(item.getInsuredValue())
                        .allocatedPremium(item.getAllocatedPremium())
                        .createdAt(Instant.now())
                        .build();
                assets.add(pa);
            }
        }
        policy.setPolicyAssets(assets);

        // Map coverages
        List<PolicyCoverage> coverages = new ArrayList<>();
        List<String> codes = List.of("COV_ACC_LOSS", "COV_THEFT", "COV_ACC_DAMAGE");
        if (quotation != null && quotation.getSelectedCoveragesJson() != null) {
            try {
                codes = objectMapper.readValue(quotation.getSelectedCoveragesJson(), List.class);
            } catch (Exception ignored) {}
        }

        for (String code : codes) {
            CoverageDefinition cov = coverageRepository.findByCoverageCode(code).orElse(null);
            if (cov != null) {
                BigDecimal covLimit = policy.getTotalSumInsured()
                        .multiply(cov.getCoverageLimitPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal dedAmount = covLimit
                        .multiply(cov.getDefaultDeductiblePercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                PolicyCoverage pc = PolicyCoverage.builder()
                        .policy(policy)
                        .coverage(cov)
                        .coverageLimit(covLimit)
                        .deductibleAmount(dedAmount)
                        .premiumCharge(policy.getAnnualPremium().divide(BigDecimal.valueOf(codes.size()), 2, RoundingMode.HALF_UP))
                        .createdAt(Instant.now())
                        .build();
                coverages.add(pc);
            }
        }
        policy.setPolicyCoverages(coverages);

        InsurancePolicy savedPolicy = policyRepository.save(policy);

        // Generate enterprise billing invoice
        billingService.createInitialPolicyInvoice(savedPolicy);

        return savedPolicy;
    }

    @Transactional(readOnly = true)
    public PolicyDTO getPolicyByNumber(String policyNumber) {
        InsurancePolicy p = policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with number: " + policyNumber));
        return mapToDTO(p);
    }

    @Transactional(readOnly = true)
    public List<PolicyDTO> getCurrentCustomerPolicies() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
        return policyRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PolicyDTO> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PolicyDTO mapToDTO(InsurancePolicy p) {
        List<PolicyDTO.PolicyAssetDTO> coveredAssets = p.getPolicyAssets().stream().map(pa ->
                PolicyDTO.PolicyAssetDTO.builder()
                        .assetId(pa.getJewelleryAsset().getAssetId())
                        .assetName(pa.getJewelleryAsset().getName())
                        .category(pa.getJewelleryAsset().getCategory())
                        .purity(pa.getJewelleryAsset().getPurity())
                        .netGoldWeight(pa.getJewelleryAsset().getNetGoldWeight())
                        .versionNumber(pa.getAssetVersionNumber())
                        .sumInsured(pa.getSumInsured())
                        .allocatedPremium(pa.getAllocatedPremium())
                        .imageUrl(pa.getJewelleryAsset().getPrimaryImageUrl())
                        .build()
        ).collect(Collectors.toList());

        List<PolicyDTO.PolicyCoverageItemDTO> coverages = p.getPolicyCoverages().stream().map(pc ->
                PolicyDTO.PolicyCoverageItemDTO.builder()
                        .coverageCode(pc.getCoverage().getCoverageCode())
                        .coverageName(pc.getCoverage().getCoverageName())
                        .coverageLimit(pc.getCoverageLimit())
                        .deductibleAmount(pc.getDeductibleAmount())
                        .premiumCharge(pc.getPremiumCharge())
                        .build()
        ).collect(Collectors.toList());

        return PolicyDTO.builder()
                .id(p.getId())
                .policyNumber(p.getPolicyNumber())
                .customerId(p.getCustomer().getId())
                .customerName(p.getCustomer().getUser().getFirstName() + " " + p.getCustomer().getUser().getLastName())
                .customerEmail(p.getCustomer().getUser().getEmail())
                .productId(p.getProduct().getId())
                .productName(p.getProduct().getProductName())
                .status(p.getStatus())
                .totalSumInsured(p.getTotalSumInsured())
                .annualPremium(p.getAnnualPremium())
                .paymentFrequency(p.getPaymentFrequency())
                .effectiveDate(p.getEffectiveDate())
                .expirationDate(p.getExpirationDate())
                .renewalCount(p.getRenewalCount())
                .policyDocumentUrl(p.getPolicyDocumentUrl())
                .issuedAt(p.getIssuedAt())
                .coveredAssets(coveredAssets)
                .coverages(coverages)
                .build();
    }
}

