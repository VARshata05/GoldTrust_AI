package com.goldtrust.workflow;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_number", nullable = false, unique = true, length = 50)
    private String activityNumber;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType; // UNDERWRITING_TASK, CLAIM_TASK, VALUATION_ALERT, REPAIR_APPROVAL, POLICY_RENEWAL

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "OPEN"; // OPEN, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "assigned_role", length = 50)
    private String assignedRole; // INSURANCE_OFFICER, JEWELLER, ADMIN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // CLAIM, POLICY, JEWELLERY, APPLICATION

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

