package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.response.NotificationDTO;
import com.sliit.echanneling.model.UserAccount;

import java.util.List;

public interface NotificationService {
    void sendNotification(UserAccount user, String title, String message);
    List<NotificationDTO> getUserNotifications(Long userId);
    void markAsRead(Long notificationId);
}
