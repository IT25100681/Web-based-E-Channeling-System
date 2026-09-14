package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.response.NotificationDTO;
import com.sliit.echanneling.model.Notification;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.model.enums.Channel;
import com.sliit.echanneling.model.enums.NotificationStatus;
import com.sliit.echanneling.repository.NotificationRepository;
import com.sliit.echanneling.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void sendNotification(UserAccount user, String title, String message) {
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Notification notification = Notification.builder()
                .userAccount(user)
                .title(title)
                .message(message)
                .channel(Channel.IN_APP)
                .status(NotificationStatus.UNREAD)
                .createdAt(nowStr)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserAccount_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> NotificationDTO.builder()
                        .notificationId(n.getNotificationId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .channel(n.getChannel())
                        .status(n.getStatus())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
    }
}
