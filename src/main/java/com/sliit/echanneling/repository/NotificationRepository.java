package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.Notification;
import com.sliit.echanneling.model.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAccount_UserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserAccount_UserIdAndStatus(Long userId, NotificationStatus status);
}
