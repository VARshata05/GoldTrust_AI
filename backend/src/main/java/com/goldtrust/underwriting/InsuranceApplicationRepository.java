package com.goldtrust.underwriting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceApplicationRepository extends JpaRepository<InsuranceApplication, Long> {
    Optional<InsuranceApplication> findByApplicationNumber(String applicationNumber);
    List<InsuranceApplication> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<InsuranceApplication> findByStatusOrderByCreatedAtDesc(String status);
}

