package com.booklink.backend.service.impl;

import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotOwnerException;
import com.booklink.backend.model.Notification;
import com.booklink.backend.model.NotificationType;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.NotificationRepository;
import com.booklink.backend.service.NotificationService;
import com.booklink.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    @Override
    public void createPostNotification(Long postAuthorId, List<Long> receiversId, Long forumId, Long postId) {

        receiversId.remove(postAuthorId);
        receiversId.forEach(receiverId -> {
            Notification notificationToSave = Notification.builder()
                    .type(NotificationType.POST)
                    .postAuthorId(postAuthorId)
                    .receiverId(receiverId)
                    .forumId(forumId)
                    .postId(postId)
                    .createdDate(new Date())
                    .build();
            notificationRepository.save(notificationToSave);
        });
    }

    @Override
    public void createCommentNotification(Long commentAuthorId, Long postAuthorId, List<Long> receiversId, Long forumId, Long postId, Long commentId) {
        receiversId.remove(commentAuthorId);
        receiversId.forEach(receiverId -> {
            Notification notificationToSave = Notification.builder()
                    .type(NotificationType.COMMENT)
                    .commentAuthorId(commentAuthorId)
                    .postAuthorId(postAuthorId)
                    .receiverId(receiverId)
                    .forumId(forumId)
                    .postId(postId)
                    .createdDate(new Date())
                    .build();
            notificationRepository.save(notificationToSave);
        });
    }

    @Override
    public List<Notification> getNotificationsEntity() {
        return notificationRepository.findAll();
    }

    @Override
    public void deleteNotification(Long id, Long userId) {
        Notification notification = getNotificationEntityById(id);
        if(notification.getReceiverId().equals(userId)){
            notificationRepository.deleteById(id);
        }
        else{
            throw new UserNotOwnerException("Solo el dueño de la notificacion puede eliminarla");
        }
    }

    @Override
    public Notification getNotificationEntityById(Long id) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);
        return notificationOptional.orElseThrow(() -> new NotFoundException("La notificacion no fue encontrada"));
    }

    @Override
    public void toggleNotification(Long forumId, Long loggedUserId) {
        User user = userService.getUserEntityById(loggedUserId);
        if(isMemberOrOwner(forumId, user)) userService.toggleUserForumNotification(loggedUserId, forumId);
    }

    private boolean isMemberOrOwner(Long forumId, User user) {
        if (user.getForumsCreated().stream().anyMatch(forum -> forum.getId().equals(forumId))) return true;
        if (user.getForumsJoined().stream().anyMatch(forum -> forum.getId().equals(forumId))) return true;
        return false;
    }

}
