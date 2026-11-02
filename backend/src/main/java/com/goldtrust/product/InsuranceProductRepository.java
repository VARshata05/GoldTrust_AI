package com.goldtrust.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {
    Optional<InsuranceProduct> findByProductCode(String productCode);
    List<InsuranceProduct> findByActiveTrue();
}

