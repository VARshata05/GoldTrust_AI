package com.goldtrust.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingAccountRepository extends JpaRepository<BillingAccount, Long> {
    Optional<BillingAccount> findByCustomerId(Long customerId);
    Optional<BillingAccount> findByAccountNumber(String accountNumber);
}

