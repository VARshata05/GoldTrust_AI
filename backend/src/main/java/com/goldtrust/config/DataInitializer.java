package com.goldtrust.config;

import com.goldtrust.billing.BillingAccount;
import com.goldtrust.billing.BillingAccountRepository;
import com.goldtrust.billing.Invoice;
import com.goldtrust.billing.InvoiceRepository;
import com.goldtrust.claim.*;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jeweller.JewellerRepository;
import com.goldtrust.jewellery.*;
import com.goldtrust.policy.*;
import com.goldtrust.product.CoverageDefinition;
import com.goldtrust.product.CoverageDefinitionRepository;
import com.goldtrust.product.InsuranceProduct;
import com.goldtrust.product.InsuranceProductRepository;
import com.goldtrust.user.Role;
import com.goldtrust.user.RoleName;
import com.goldtrust.user.RoleRepository;
import com.goldtrust.user.User;
import com.goldtrust.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final JewellerRepository jewellerRepository;
    private final JewelleryAssetRepository assetRepository;
    private final AssetVersionRepository versionRepository;
    private final TransformationEventRepository transformationRepository;
    private final InsuranceProductRepository productRepository;
    private final CoverageDefinitionRepository coverageRepository;
    private final InsurancePolicyRepository policyRepository;
    private final BillingAccountRepository billingAccountRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClaimRepository claimRepository;
    private final ClaimAssessmentRepository assessmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting GoldTrust AI Data Initializer...");

        // Ensure roles exist
        Role roleCustomer = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_CUSTOMER).description("Customer").build()));
        Role roleJeweller = roleRepository.findByName(RoleName.ROLE_JEWELLER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_JEWELLER).description("Jeweller").build()));
        Role roleOfficer = roleRepository.findByName(RoleName.ROLE_INSURANCE_OFFICER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_INSURANCE_OFFICER).description("Officer").build()));
        Role roleAdmin = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).description("Admin").build()));

        // 1. Create Demo Admin
        User adminUser = userRepository.findByUsername("admin").orElseGet(() -> {
            User u = User.builder()
                    .username("admin")
                    .email("admin@goldtrust.ai")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Rajesh")
                    .lastName("Sharma")
                    .phoneNumber("+91 98200 11001")
                    .roles(Set.of(roleAdmin))
                    .active(true)
                    .build();
            return userRepository.save(u);
        });

        // 2. Create Demo Insurance Officer
        User officerUser = userRepository.findByUsername("officer").orElseGet(() -> {
            User u = User.builder()
                    .username("officer")
                    .email("officer@goldtrust.ai")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Vikram")
                    .lastName("Aditya")
                    .phoneNumber("+91 98200 22002")
                    .roles(Set.of(roleOfficer))
                    .active(true)
                    .build();
            return userRepository.save(u);
        });

        // 3. Create Demo Jeweller
        User jewellerUser = userRepository.findByUsername("jeweller").orElseGet(() -> {
            User u = User.builder()
                    .username("jeweller")
                    .email("jeweller@goldtrust.ai")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Suresh")
                    .lastName("Verma")
                    .phoneNumber("+91 98200 33003")
                    .roles(Set.of(roleJeweller))
                    .active(true)
                    .build();
            return userRepository.save(u);
        });

        Jeweller jeweller = jewellerRepository.findByUserId(jewellerUser.getId()).orElseGet(() -> {
            Jeweller j = Jeweller.builder()
                    .user(jewellerUser)
                    .jewellerCode("JWL-MUM-001")
                    .businessName("Tanishq Hallmark & Heritage Care Centre")
                    .licenseNumber("BIS-HM-2024-99881")
                    .bisHallmarkCenterId("AHC-MUM-4001")
                    .certified(true)
                    .contactPerson("Suresh Verma")
                    .phone("+91 98200 33003")
                    .email("tanishq.heritage@goldtrust.ai")
                    .address("45 Zaveri Bazaar, Marine Lines")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .rating(BigDecimal.valueOf(4.90))
                    .verificationStatus("ACTIVE")
                    .build();
            return jewellerRepository.save(j);
        });

        // 4. Create Demo Customer
        User customerUser = userRepository.findByUsername("customer").orElseGet(() -> {
            User u = User.builder()
                    .username("customer")
                    .email("customer@goldtrust.ai")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Priya")
                    .lastName("Mehta")
                    .phoneNumber("+91 98200 44004")
                    .roles(Set.of(roleCustomer))
                    .active(true)
                    .build();
            return userRepository.save(u);
        });

        Customer customer = customerRepository.findByUserId(customerUser.getId()).orElseGet(() -> {
            Customer c = Customer.builder()
                    .user(customerUser)
                    .customerNumber("GT-CUST-000001")
                    .idType("NATIONAL_ID")
                    .idNumber("AADHAAR-8839-2910-1122")
                    .addressLine1("Flat 402, Sea Crest Towers")
                    .addressLine2("Worli Sea Face")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .postalCode("400030")
                    .country("India")
                    .kycStatus("VERIFIED")
                    .riskScore(BigDecimal.valueOf(1.00))
                    .build();
            return customerRepository.save(c);
        });

        // Seed Billing Account for Customer
        BillingAccount billingAccount = billingAccountRepository.findByCustomerId(customer.getId()).orElseGet(() -> {
            BillingAccount b = BillingAccount.builder()
                    .accountNumber("GT-BIL-2026-000001")
                    .customer(customer)
                    .currentBalance(BigDecimal.valueOf(1234.00))
                    .totalBilled(BigDecimal.valueOf(5420.00))
                    .totalPaid(BigDecimal.valueOf(4186.00))
                    .status("CURRENT")
                    .build();
            return billingAccountRepository.save(b);
        });

        // 5. Seed Jewellery Assets
        if (assetRepository.count() == 0) {
            JewelleryAsset asset1 = JewelleryAsset.builder()
                    .assetId("GT-JWL-2026-000001")
                    .customer(customer)
                    .name("Royal Heritage Kundan Choker Necklace")
                    .category("Necklace")
                    .purity("22K")
                    .netGoldWeight(BigDecimal.valueOf(45.500))
                    .grossWeight(BigDecimal.valueOf(52.200))
                    .huid("HN7291")
                    .hallmarkStatus("VERIFIED")
                    .purchaseDate(LocalDate.now().minusMonths(14))
                    .purchasePrice(BigDecimal.valueOf(325000.00))
                    .appraisalValue(BigDecimal.valueOf(340000.00))
                    .currentEstimatedValue(BigDecimal.valueOf(365000.00))
                    .primaryImageUrl("https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=600&q=80")
                    .ownershipStatus("OWNED")
                    .verificationStatus("VERIFIED")
                    .verifiedByJeweller(jeweller)
                    .verifiedAt(Instant.now().minusSeconds(86400 * 30))
                    .currentVersion(2)
                    .active(true)
                    .build();
            JewelleryAsset savedAsset1 = assetRepository.save(asset1);

            // Asset 1 Version 1
            versionRepository.save(AssetVersion.builder()
                    .jewelleryAsset(savedAsset1)
                    .versionNumber(1)
                    .name(savedAsset1.getName())
                    .category(savedAsset1.getCategory())
                    .purity(savedAsset1.getPurity())
                    .netGoldWeight(BigDecimal.valueOf(46.000))
                    .grossWeight(BigDecimal.valueOf(52.700))
                    .huid("HN7291")
                    .hallmarkStatus("VERIFIED")
                    .estimatedValue(BigDecimal.valueOf(330000.00))
                    .primaryImageUrl(savedAsset1.getPrimaryImageUrl())
                    .changeReason("Original Registration")
                    .createdBy("customer")
                    .build());

            // Asset 1 Version 2 (Resizing & Prong restoration)
            versionRepository.save(AssetVersion.builder()
                    .jewelleryAsset(savedAsset1)
                    .versionNumber(2)
                    .name(savedAsset1.getName())
                    .category(savedAsset1.getCategory())
                    .purity(savedAsset1.getPurity())
                    .netGoldWeight(BigDecimal.valueOf(45.500))
                    .grossWeight(BigDecimal.valueOf(52.200))
                    .huid("HN7291")
                    .hallmarkStatus("VERIFIED")
                    .estimatedValue(BigDecimal.valueOf(365000.00))
                    .primaryImageUrl(savedAsset1.getPrimaryImageUrl())
                    .changeReason("Certified Resizing & Clasp Adjustment at Tanishq")
                    .createdBy("jeweller")
                    .build());

            // Transformation Events for Asset 1
            transformationRepository.save(TransformationEvent.builder()
                    .jewelleryAsset(savedAsset1)
                    .transformationType("REGISTRATION")
                    .previousVersion(1)
                    .newVersion(1)
                    .previousWeight(BigDecimal.valueOf(46.000))
                    .newWeight(BigDecimal.valueOf(46.000))
                    .previousPurity("22K")
                    .newPurity("22K")
                    .jeweller(jeweller)
                    .verificationStatus("VERIFIED")
                    .description("Digital passport registered and certified with BIS hallmark.")
                    .createdBy("customer")
                    .build());

            transformationRepository.save(TransformationEvent.builder()
                    .jewelleryAsset(savedAsset1)
                    .transformationType("RESIZING")
                    .previousVersion(1)
                    .newVersion(2)
                    .previousWeight(BigDecimal.valueOf(46.000))
                    .newWeight(BigDecimal.valueOf(45.500))
                    .previousPurity("22K")
                    .newPurity("22K")
                    .jeweller(jeweller)
                    .verificationStatus("VERIFIED")
                    .description("Clasp adjustment and minor chain segment removal (-0.5g). New version recorded.")
                    .createdBy("jeweller")
                    .build());

            // Asset 2: Solitaire Diamond Gold Ring
            JewelleryAsset asset2 = JewelleryAsset.builder()
                    .assetId("GT-JWL-2026-000002")
                    .customer(customer)
                    .name("Solitaire Diamond 18K Yellow Gold Ring")
                    .category("Ring")
                    .purity("18K")
                    .netGoldWeight(BigDecimal.valueOf(8.250))
                    .grossWeight(BigDecimal.valueOf(8.650))
                    .huid("RG4482")
                    .hallmarkStatus("VERIFIED")
                    .purchaseDate(LocalDate.now().minusMonths(8))
                    .purchasePrice(BigDecimal.valueOf(85000.00))
                    .appraisalValue(BigDecimal.valueOf(92000.00))
                    .currentEstimatedValue(BigDecimal.valueOf(98500.00))
                    .primaryImageUrl("https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=600&q=80")
                    .ownershipStatus("OWNED")
                    .verificationStatus("VERIFIED")
                    .verifiedByJeweller(jeweller)
                    .verifiedAt(Instant.now().minusSeconds(86400 * 20))
                    .currentVersion(1)
                    .active(true)
                    .build();
            JewelleryAsset savedAsset2 = assetRepository.save(asset2);

            versionRepository.save(AssetVersion.builder()
                    .jewelleryAsset(savedAsset2)
                    .versionNumber(1)
                    .name(savedAsset2.getName())
                    .category(savedAsset2.getCategory())
                    .purity(savedAsset2.getPurity())
                    .netGoldWeight(savedAsset2.getNetGoldWeight())
                    .grossWeight(savedAsset2.getGrossWeight())
                    .huid(savedAsset2.getHuid())
                    .hallmarkStatus(savedAsset2.getHallmarkStatus())
                    .estimatedValue(savedAsset2.getCurrentEstimatedValue())
                    .primaryImageUrl(savedAsset2.getPrimaryImageUrl())
                    .changeReason("Initial Asset Registration")
                    .createdBy("customer")
                    .build());

            transformationRepository.save(TransformationEvent.builder()
                    .jewelleryAsset(savedAsset2)
                    .transformationType("REGISTRATION")
                    .previousVersion(1)
                    .newVersion(1)
                    .previousWeight(savedAsset2.getNetGoldWeight())
                    .newWeight(savedAsset2.getNetGoldWeight())
                    .previousPurity("18K")
                    .newPurity("18K")
                    .jeweller(jeweller)
                    .verificationStatus("VERIFIED")
                    .description("Digital passport registered.")
                    .createdBy("customer")
                    .build());

            // 6. Seed Demo Policy
            InsuranceProduct product = productRepository.findByProductCode("GSC-01")
                    .orElseGet(() -> productRepository.findAll().get(0));

            InsurancePolicy policy = InsurancePolicy.builder()
                    .policyNumber("GT-POL-2026-000001")
                    .customer(customer)
                    .product(product)
                    .status("ACTIVE")
                    .totalSumInsured(BigDecimal.valueOf(450000.00))
                    .annualPremium(BigDecimal.valueOf(7425.00))
                    .paymentFrequency("ANNUAL")
                    .effectiveDate(LocalDate.now().minusMonths(2))
                    .expirationDate(LocalDate.now().plusMonths(10))
                    .policyDocumentUrl("/documents/policies/GT-POL-2026-000001.pdf")
                    .renewalCount(0)
                    .issuedByUser(officerUser)
                    .issuedAt(Instant.now().minusSeconds(86400 * 60))
                    .build();

            // Policy Assets
            PolicyAsset pa1 = PolicyAsset.builder()
                    .policy(policy)
                    .jewelleryAsset(savedAsset1)
                    .assetVersionNumber(savedAsset1.getCurrentVersion())
                    .sumInsured(BigDecimal.valueOf(355000.00))
                    .allocatedPremium(BigDecimal.valueOf(5860.00))
                    .build();

            PolicyAsset pa2 = PolicyAsset.builder()
                    .policy(policy)
                    .jewelleryAsset(savedAsset2)
                    .assetVersionNumber(savedAsset2.getCurrentVersion())
                    .sumInsured(BigDecimal.valueOf(95000.00))
                    .allocatedPremium(BigDecimal.valueOf(1565.00))
                    .build();

            policy.setPolicyAssets(List.of(pa1, pa2));

            // Policy Coverages
            List<PolicyCoverage> policyCovList = new ArrayList<>();
            List<CoverageDefinition> covDefs = coverageRepository.findByEnabledTrue();
            for (CoverageDefinition cd : covDefs) {
                policyCovList.add(PolicyCoverage.builder()
                        .policy(policy)
                        .coverage(cd)
                        .coverageLimit(policy.getTotalSumInsured())
                        .deductibleAmount(BigDecimal.valueOf(5000.00))
                        .premiumCharge(BigDecimal.valueOf(825.00))
                        .build());
            }
            policy.setPolicyCoverages(policyCovList);
            InsurancePolicy savedPolicy = policyRepository.save(policy);

            // Policy Invoice
            invoiceRepository.save(Invoice.builder()
                    .invoiceNumber("GT-INV-2026-000001")
                    .billingAccount(billingAccount)
                    .policy(savedPolicy)
                    .invoiceType("NEW_POLICY")
                    .amount(savedPolicy.getAnnualPremium())
                    .taxAmount(BigDecimal.valueOf(1336.50))
                    .totalAmount(BigDecimal.valueOf(8761.50))
                    .dueDate(LocalDate.now().minusMonths(1))
                    .status("PAID")
                    .notes("Annual Comprehensive Policy Premium")
                    .build());

            // 7. Seed Demo Claim
            Claim demoClaim = Claim.builder()
                    .claimNumber("GT-CLM-2026-000001")
                    .policy(savedPolicy)
                    .jewelleryAsset(savedAsset1)
                    .customer(customer)
                    .incidentType("DAMAGE")
                    .incidentDate(LocalDate.now().minusDays(5))
                    .incidentLocation("Bandra Kurla Complex, Mumbai")
                    .incidentDescription("Accidental impact during wedding event resulted in gemstone dislodgement and slight bending of side clasp.")
                    .claimedAmount(BigDecimal.valueOf(18500.00))
                    .approvedAmount(BigDecimal.valueOf(18500.00))
                    .deductibleAmount(BigDecimal.valueOf(925.00))
                    .netSettlementAmount(BigDecimal.valueOf(17575.00))
                    .status("APPROVED")
                    .settlementPreference("REPAIR")
                    .preferredJeweller(jeweller)
                    .assignedOfficer(officerUser)
                    .submissionDate(Instant.now().minusSeconds(86400 * 4))
                    .build();
            Claim savedClaim = claimRepository.save(demoClaim);

            assessmentRepository.save(ClaimAssessment.builder()
                    .claim(savedClaim)
                    .assessorUser(officerUser)
                    .policyValidityConfirmed(true)
                    .coverageConfirmed(true)
                    .applicableCoverageCode("COV_ACC_DAMAGE")
                    .deductiblePercentageApplied(BigDecimal.valueOf(5.00))
                    .identityConsistencyStatus("CONSISTENT")
                    .anomalyDetected(false)
                    .anomalyScore(BigDecimal.valueOf(0.04))
                    .aiConfidenceScore(BigDecimal.valueOf(96.80))
                    .recommendation("APPROVE_REPAIR")
                    .rationale("TAGE Engine verified visual identity against asset passport v2. Gold weight and HUID HN7291 confirmed authentic. Damage is accidental and eligible under Accidental Damage Coverage.")
                    .build());
        }

        log.info("GoldTrust AI demo data successfully seeded!");
    }
}

