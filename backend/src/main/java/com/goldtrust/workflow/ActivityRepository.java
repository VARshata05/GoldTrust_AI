package com.goldtrust.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByActivityNumber(String activityNumber);
    List<Activity> findByAssignedRoleAndStatusOrderByDueDateAsc(String assignedRole, String status);
    List<Activity> findByAssignedUserIdAndStatusOrderByDueDateAsc(Long assignedUserId, String status);
    List<Activity> findByStatusOrderByCreatedAtDesc(String status);
}

