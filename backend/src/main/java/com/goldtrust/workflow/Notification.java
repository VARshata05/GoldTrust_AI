package com.goldtrust.workflow;

import com.goldtrust.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType; // COVERAGE_GAP, UNDERWRITING_UPDATE, CLAIM_STATUS, PAYMENT_DUE, REPAIR_UPDATE, SYSTEM

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

