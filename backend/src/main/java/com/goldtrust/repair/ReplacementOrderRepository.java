package com.goldtrust.repair;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplacementOrderRepository extends JpaRepository<ReplacementOrder, Long> {
    Optional<ReplacementOrder> findByOrderNumber(String orderNumber);
    List<ReplacementOrder> findByClaimId(Long claimId);
    List<ReplacementOrder> findByJewellerId(Long jewellerId);
}

