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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Long postAuthorId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Long commentAuthorId;

    private final Long forumId;
    private final Long postId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Long commentId;

    private final Date date;

    public static NotificationViewDto from(Notification notification, String notificationCreatorUsername, String forumName) {
        if (notification.getType().equals(NotificationType.POST))
            return NotificationViewDto.builder()
                    .content("@%s creó una nueva publicación en %s!".formatted(notificationCreatorUsername, forumName))
                    .postAuthorId(notification.getPostAuthorId())
                    .forumId(notification.getForumId())
                    .postId(notification.getPostId())
                    .date(notification.getCreatedDate())
                    .build();
        else if (NotificationType.COMMENT.equals(notification.getType())) {
            return NotificationViewDto.builder()
                    .content("@%s creó un nuevo comentario en %s!".formatted(notificationCreatorUsername, forumName))
                    .commentAuthorId(notification.getCommentAuthorId())
                    .forumId(notification.getForumId())
                    .postId(notification.getPostId())
                    .commentId(notification.getCommentId())
                    .date(notification.getCreatedDate())
                    .build();
        }
        throw new RuntimeException("Invalid notification type");
    }
}


