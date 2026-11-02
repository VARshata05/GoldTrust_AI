package com.goldtrust.valuation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GoldRateRepository extends JpaRepository<GoldRate, Long> {
    Optional<GoldRate> findFirstByActiveTrueOrderByEffectiveDateDesc();
    Optional<GoldRate> findByEffectiveDate(LocalDate date);
}

