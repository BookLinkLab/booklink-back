package com.booklink.backend.service;

import com.booklink.backend.dto.notification.ForumNotificationDto;
import com.booklink.backend.dto.notification.NotificationViewDto;
import com.booklink.backend.model.Notification;

import java.util.List;


public interface NotificationService {
    void createPostNotification(Long postAuthorId, List<Long> receiversId, Long forumId, Long postId);

    void createCommentNotification(Long commentAuthorId, Long postAuthorId, List<Long> receiversId, Long forumId, Long postId, Long commentId);

    List<Notification> getNotificationsEntity();

    List<Notification> getNotificationsEntityByUserId(Long userId);

    List<NotificationViewDto> getNotificationsByUserId(Long userId);

    void deleteNotification(Long id, Long userId);

    Notification getNotificationEntityById(Long id);

    boolean toggleForumNotification(Long forumId, Long loggedUserId);

    boolean markNotificationAsSeen(Long notificationId, Long userId);

    List<ForumNotificationDto> getUserNotificationConfiguration(Long loggedUserId);
}
