package com.booklink.backend.controller;

import com.booklink.backend.service.ForumService;
import com.booklink.backend.service.NotificationService;
import com.booklink.backend.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    public NotificationController(NotificationService notificationService1, SecurityUtil securityUtil) {
        this.notificationService = notificationService1;
        this.securityUtil = securityUtil;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Notificacion eliminada");
    }


}
