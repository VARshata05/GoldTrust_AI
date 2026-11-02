package com.goldtrust.underwriting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnderwritingAssessmentRepository extends JpaRepository<UnderwritingAssessment, Long> {
    List<UnderwritingAssessment> findByApplicationIdOrderByAssessedAtDesc(Long applicationId);
}

