package com.goldtrust.ai;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiAssessmentRepository extends JpaRepository<AiAssessment, Long> {
    Optional<AiAssessment> findByAssessmentReference(String assessmentReference);
    List<AiAssessment> findByTargetEntityTypeAndTargetEntityIdOrderByCreatedAtDesc(String targetEntityType, Long targetEntityId);
    Optional<AiAssessment> findFirstByTargetEntityTypeAndTargetEntityIdOrderByCreatedAtDesc(String targetEntityType, Long targetEntityId);
}

