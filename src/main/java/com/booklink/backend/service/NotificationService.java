package com.booklink.backend.service;

import com.booklink.backend.dto.notification.NotificationViewDto;
import com.booklink.backend.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;


public interface NotificationService {
    void createPostNotification(Long postAuthorId, List<Long> receiversId, Long forumId, Long postId);

    void createCommentNotification(Long commentAuthorId, Long postAuthorId, List<Long> receiversId, Long forumId, Long postId, Long commentId);

    List<Notification> getNotificationsEntity();

    List<NotificationViewDto> getNotificationsByUserId(Long userId);

    void deleteNotification(Long id, Long userId);

    Notification getNotificationEntityById(Long id);
}
