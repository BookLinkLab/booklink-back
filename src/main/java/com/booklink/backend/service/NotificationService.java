package com.booklink.backend.service;

import com.booklink.backend.model.Notification;

import java.util.List;


public interface NotificationService {
    void createPostNotification(Long postAuthorId, List<Long> receiversId, Long forumId, Long postId);
    void createCommentNotification(Long commentAuthorId, Long postAuthorId, List<Long> receiversId, Long forumId, Long postId, Long commentId);

    List<Notification> getNotificationsEntity();

    void deleteNotification(Long id, Long userId);

    Notification getNotificationEntityById(Long id);

    void toggleForumNotification(Long forumId, Long loggedUserId);
}
