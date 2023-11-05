package com.booklink.backend.service.impl;

import com.booklink.backend.dto.forum.ForumDtoFactory;
import com.booklink.backend.dto.notification.NotificationViewDto;
import com.booklink.backend.exception.MemberDoesntBelongForumException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotOwnerException;
import com.booklink.backend.model.Notification;
import com.booklink.backend.model.NotificationType;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.NotificationRepository;
import com.booklink.backend.service.NotificationService;
import com.booklink.backend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final ForumDtoFactory forumDtoFactory;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserService userService, ForumDtoFactory forumDtoFactory) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.forumDtoFactory = forumDtoFactory;
    }

    @Override
    public void createPostNotification(Long postAuthorId, List<Long> receiversId, Long forumId, Long postId) {

        receiversId.remove(postAuthorId);
        receiversId.forEach(receiverId -> {
            if(isForumNotificationActive(receiverId,forumId)){
            Notification notificationToSave = Notification.builder()
                    .type(NotificationType.POST)
                    .postAuthorId(postAuthorId)
                    .receiverId(receiverId)
                    .forumId(forumId)
                    .postId(postId)
                    .createdDate(new Date())
                    .build();
            notificationRepository.save(notificationToSave);
        }});
    }

    @Override
    public void createCommentNotification(Long commentAuthorId, Long postAuthorId, List<Long> receiversId, Long forumId, Long postId, Long commentId) {
        receiversId.remove(commentAuthorId);
        receiversId.forEach(receiverId -> {
            if(isForumNotificationActive(receiverId,forumId)){
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
        }});
    }

    @Override
    public List<Notification> getNotificationsEntity() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsEntityByUserId(Long userId) {
        return notificationRepository.findAllByReceiverId(userId);
    }

    @Override
    public List<NotificationViewDto> getNotificationsByUserId(Long userId) {
        List<Notification> userNotifications = getNotificationsEntityByUserId(userId);

        return userNotifications.stream()
                .map(notification -> {
                    String notificationCreatorUsername;
                    String forumName = notification.getForum().getName();
                    if (notification.getType().equals(NotificationType.POST)) {
                        notificationCreatorUsername = notification.getPostAuthor().getUsername();
                    } else {
                        notificationCreatorUsername = notification.getCommentAuthor().getUsername();
                    }
                    return NotificationViewDto.from(notification, notificationCreatorUsername, forumName);
                }).toList();
    }

    @Override
    public void deleteNotification(Long id, Long userId) {
        Notification notification = getNotificationEntityById(id);
        if (notification.getReceiverId().equals(userId)) {
            notificationRepository.deleteById(id);
        } else {
            throw new UserNotOwnerException("Solo el dueño de la notificacion puede eliminarla");
        }
    }

    @Override
    public Notification getNotificationEntityById(Long id) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);
        return notificationOptional.orElseThrow(() -> new NotFoundException("La notificacion no fue encontrada"));
    }

    @Override
    @Transactional
    public boolean toggleForumNotification(Long forumId, Long loggedUserId) {
        if(this.forumDtoFactory.isMember(forumId,loggedUserId) || this.forumDtoFactory.isForumOwner(forumId,loggedUserId) ) return userService.toggleUserForumNotification(loggedUserId, forumId);
        else throw new MemberDoesntBelongForumException("El usuario no pertenece al foro");
    }

    @Override
    public boolean markNotificationAsSeen(Long notificationId, Long userId){
        Notification notification = getNotificationEntityById(notificationId);
        if(notification.getReceiverId().equals(userId)){
            notification.setSeen(true);
            Notification savedNotification = notificationRepository.save(notification);
            return savedNotification.isSeen();
        }else{
            throw new UserNotOwnerException("Solo el recipiente de la notificacion puede marcarla como vista");
        }
    }

    @Override
    public Integer getNotificationsNotSeenCount(Long loggedUserId) {
        return this.getNotificationsEntityByUserId(loggedUserId).stream().filter(notification -> !notification.isSeen()).toList().size();
    }

    private boolean isForumNotificationActive(Long userID, Long forumID){
         User user = userService.getUserEntityById(userID);
         List<Long> activatedForums = user.getForumNotifications();
         return activatedForums.contains(forumID);
    }

}
