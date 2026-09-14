package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.response.NotificationDTO;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.service.NotificationService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserAccountService userAccountService;

    @GetMapping
    public String viewNotifications(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        List<NotificationDTO> list = notificationService.getUserNotifications(account.getUserId());
        model.addAttribute("notifications", list);
        return "fragments/notifications-bell :: notificationList";
    }

    @PostMapping("/{id}/read")
    @ResponseBody
    public String markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return "OK";
    }
}
