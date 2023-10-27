package com.booklink.backend.controller;

import com.booklink.backend.dto.notification.NotificationViewDto;
import com.booklink.backend.service.NotificationService;
import com.booklink.backend.utils.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    public NotificationController(NotificationService notificationService1, SecurityUtil securityUtil) {
        this.notificationService = notificationService1;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public ResponseEntity<List<NotificationViewDto>> getNotifications() {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getNotificationsByUserId(securityUtil.getLoggedUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Notificación eliminada");
    }
}
