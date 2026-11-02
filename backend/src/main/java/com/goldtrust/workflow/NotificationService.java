package com.goldtrust.workflow;

import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.security.UserPrincipal;
import com.goldtrust.user.User;
import com.goldtrust.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Notification createNotification(User user, String title, String message, String type, String actionUrl) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .actionUrl(actionUrl)
                .read(false)
                .createdAt(Instant.now())
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getCurrentUserNotifications() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(principal.getId());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepository.countByUserIdAndReadFalse(principal.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        n.setRead(true);
        notificationRepository.save(n);
    }
}

