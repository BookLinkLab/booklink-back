package com.booklink.backend.dto.notification;

import com.booklink.backend.model.Notification;
import com.booklink.backend.model.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class NotificationViewDto {
    private final String content;
    private final Long authorId;
    private final Long forumId;
    private final Long postId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Long commentId;

    private final Date date;

    public static NotificationViewDto from(Notification notification, String notificationCreatorUsername, String forumName) {
        NotificationType notificationType = notification.getType();
        switch (notificationType) {
            case POST -> {
                return NotificationViewDto.builder()
                        .content("@%s creó una nueva publicación en \"%s\"!".formatted(notificationCreatorUsername, forumName))
                        .authorId(notification.getPostAuthorId())
                        .forumId(notification.getForumId())
                        .postId(notification.getPostId())
                        .date(notification.getCreatedDate())
                        .build();
            }
            case COMMENT -> {
                return NotificationViewDto.builder()
                        .content("@%s creó un nuevo comentario en \"%s\"!".formatted(notificationCreatorUsername, forumName))
                        .authorId(notification.getCommentAuthorId())
                        .forumId(notification.getForumId())
                        .postId(notification.getPostId())
                        .commentId(notification.getCommentId())
                        .date(notification.getCreatedDate())
                        .build();
            }
            default -> throw new RuntimeException("Invalid notification type");
        }
    }
}
