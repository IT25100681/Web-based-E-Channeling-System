package com.sliit.echanneling.dto.response;

import com.sliit.echanneling.model.enums.Channel;
import com.sliit.echanneling.model.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private String title;
    private String message;
    private Channel channel;
    private NotificationStatus status;
    private String createdAt;
}
