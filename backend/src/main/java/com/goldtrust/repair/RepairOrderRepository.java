package com.goldtrust.repair;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    Optional<RepairOrder> findByOrderNumber(String orderNumber);
    List<RepairOrder> findByClaimId(Long claimId);
    List<RepairOrder> findByJewellerId(Long jewellerId);
    List<RepairOrder> findByStatus(String status);
}

