package com.goldtrust.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoverageDefinitionRepository extends JpaRepository<CoverageDefinition, Long> {
    Optional<CoverageDefinition> findByCoverageCode(String coverageCode);
    List<CoverageDefinition> findByEnabledTrue();
}

