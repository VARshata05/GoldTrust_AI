package com.goldtrust.underwriting;

import com.goldtrust.common.BadRequestException;
import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.policy.InsurancePolicy;
import com.goldtrust.policy.PolicyService;
import com.goldtrust.quotation.Quotation;
import com.goldtrust.quotation.QuotationRepository;
import com.goldtrust.security.UserPrincipal;
import com.goldtrust.user.User;
import com.goldtrust.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnderwritingService {

    private final InsuranceApplicationRepository applicationRepository;
    private final UnderwritingAssessmentRepository assessmentRepository;
    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PolicyService policyService;

    public UnderwritingService(
            InsuranceApplicationRepository applicationRepository,
            UnderwritingAssessmentRepository assessmentRepository,
            QuotationRepository quotationRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            @Lazy PolicyService policyService) {
        this.applicationRepository = applicationRepository;
        this.assessmentRepository = assessmentRepository;
        this.quotationRepository = quotationRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.policyService = policyService;
    }

    @Transactional
    public ApplicationDTO submitApplication(Long quotationId, String customerNotes) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with ID: " + quotationId));

        if (!quotation.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("Unauthorized access to quotation");
        }

        long count = applicationRepository.count() + 1;
        String appNum = String.format("GT-APP-2026-%06d", count);

        InsuranceApplication app = InsuranceApplication.builder()
                .applicationNumber(appNum)
                .customer(customer)
                .quotation(quotation)
                .product(quotation.getProduct())
                .totalInsuredValue(quotation.getInsuredValue())
                .proposedPremium(quotation.getTotalAnnualPremium())
                .status("UNDERWRITING")
                .customerNotes(customerNotes)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        InsuranceApplication saved = applicationRepository.save(app);
        quotation.setStatus("CONVERTED_TO_APPLICATION");
        quotationRepository.save(quotation);

        return mapToDTO(saved);
    }

    @Transactional
    public ApplicationDTO processDecision(Long applicationId, UnderwritingDecisionRequest request) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User officer = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer user not found"));

        InsuranceApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        String decision = request.getDecision().toUpperCase();
        if (!List.of("APPROVE", "REFER", "DECLINE", "REQUEST_INFORMATION").contains(decision)) {
            throw new BadRequestException("Invalid decision: " + decision + ". Allowed: APPROVE, REFER, DECLINE, REQUEST_INFORMATION");
        }

        BigDecimal jwlScore = request.getJewelleryVerificationScore() != null ? request.getJewelleryVerificationScore() : BigDecimal.valueOf(95.0);
        BigDecimal appScore = request.getAppraisalValidityScore() != null ? request.getAppraisalValidityScore() : BigDecimal.valueOf(92.0);
        BigDecimal custScore = request.getCustomerRiskScore() != null ? request.getCustomerRiskScore() : BigDecimal.valueOf(10.0); // 10/100 risk
        BigDecimal aiScore = request.getAiRiskAssessmentScore() != null ? request.getAiRiskAssessmentScore() : BigDecimal.valueOf(88.0);
        BigDecimal overallScore = BigDecimal.valueOf(15.0); // low risk index

        UnderwritingAssessment assessment = UnderwritingAssessment.builder()
                .application(app)
                .officer(officer)
                .decision(decision)
                .jewelleryVerificationScore(jwlScore)
                .appraisalValidityScore(appScore)
                .customerRiskScore(custScore)
                .aiRiskAssessmentScore(aiScore)
                .overallRiskScore(overallScore)
                .decisionReasons(request.getReasons())
                .stipulations(request.getStipulations())
                .assessedAt(Instant.now())
                .build();
        assessmentRepository.save(assessment);

        app.setUnderwritingDecision(decision);
        app.setAssignedOfficer(officer);
        app.setDecisionDate(Instant.now());
        app.setUpdatedAt(Instant.now());

        if ("APPROVE".equalsIgnoreCase(decision)) {
            app.setStatus("APPROVED");
            applicationRepository.save(app);
            // Issue active enterprise policy
            policyService.issuePolicyFromApplication(app);
        } else if ("DECLINE".equalsIgnoreCase(decision)) {
            app.setStatus("DECLINED");
            applicationRepository.save(app);
        } else if ("REFER".equalsIgnoreCase(decision)) {
            app.setStatus("REFERRED");
            applicationRepository.save(app);
        } else if ("REQUEST_INFORMATION".equalsIgnoreCase(decision)) {
            app.setStatus("REQUEST_INFORMATION");
            applicationRepository.save(app);
        }

        return mapToDTO(app);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getUnderwritingQueue() {
        return applicationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(Long id) {
        InsuranceApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        return mapToDTO(app);
    }

    private ApplicationDTO mapToDTO(InsuranceApplication app) {
        List<ApplicationDTO.UnderwritingAssessmentDTO> assessments = assessmentRepository
                .findByApplicationIdOrderByAssessedAtDesc(app.getId()).stream()
                .map(a -> ApplicationDTO.UnderwritingAssessmentDTO.builder()
                        .id(a.getId())
                        .decision(a.getDecision())
                        .jewelleryVerificationScore(a.getJewelleryVerificationScore())
                        .appraisalValidityScore(a.getAppraisalValidityScore())
                        .customerRiskScore(a.getCustomerRiskScore())
                        .aiRiskAssessmentScore(a.getAiRiskAssessmentScore())
                        .overallRiskScore(a.getOverallRiskScore())
                        .decisionReasons(a.getDecisionReasons())
                        .stipulations(a.getStipulations())
                        .assessedAt(a.getAssessedAt())
                        .build())
                .collect(Collectors.toList());

        return ApplicationDTO.builder()
                .id(app.getId())
                .applicationNumber(app.getApplicationNumber())
                .customerId(app.getCustomer().getId())
                .customerName(app.getCustomer().getUser().getFirstName() + " " + app.getCustomer().getUser().getLastName())
                .customerEmail(app.getCustomer().getUser().getEmail())
                .quotationNumber(app.getQuotation() != null ? app.getQuotation().getQuotationNumber() : null)
                .productId(app.getProduct().getId())
                .productName(app.getProduct().getProductName())
                .totalInsuredValue(app.getTotalInsuredValue())
                .proposedPremium(app.getProposedPremium())
                .status(app.getStatus())
                .customerNotes(app.getCustomerNotes())
                .underwritingDecision(app.getUnderwritingDecision())
                .assignedOfficerId(app.getAssignedOfficer() != null ? app.getAssignedOfficer().getId() : null)
                .assignedOfficerName(app.getAssignedOfficer() != null ? app.getAssignedOfficer().getFirstName() + " " + app.getAssignedOfficer().getLastName() : null)
                .decisionDate(app.getDecisionDate())
                .createdAt(app.getCreatedAt())
                .assessments(assessments)
                .build();
    }
}

