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
    private final Long notificationId;
    private final String content;
    private final Long authorId;
    private final String authorName;
    private final Long forumId;
    private final String forumName;
    private final Long postId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Long commentId;

    private final Date date;
    private final boolean seen;
    private final String img;

    public static NotificationViewDto from(Notification notification, String notificationCreatorUsername, String forumName) {
        NotificationType notificationType = notification.getType();
        switch (notificationType) {
            case POST -> {
                String authorName = notification.getPostAuthor().getUsername();

                return NotificationViewDto.builder()
                        .notificationId(notification.getId())
                        .authorName(notificationCreatorUsername)
                        .content(authorName+" creó una nueva publicación en "+"\""+forumName+"\"!")
                        .authorId(notification.getPostAuthorId())
                        .forumId(notification.getForumId())
                        .forumName(forumName)
                        .postId(notification.getPostId())
                        .date(notification.getCreatedDate())
                        .seen(notification.isSeen())
                        .img(notification.getForum().getImg())
                        .build();
            }
            case COMMENT -> {
                String authorName = notification.getPostAuthor().getUsername();

                return NotificationViewDto.builder()
                        .notificationId(notification.getId())
                        .authorName(notificationCreatorUsername)
                        .content(authorName+" creó un nuevo comentario en "+"\""+forumName+"\"!")
                        .authorId(notification.getCommentAuthorId())
                        .forumId(notification.getForumId())
                        .forumName(forumName)
                        .postId(notification.getPostId())
                        .commentId(notification.getCommentId())
                        .date(notification.getCreatedDate())
                        .seen(notification.isSeen())
                        .img(notification.getForum().getImg())
                        .build();
            }
            default -> throw new RuntimeException("Invalid notification type");
        }
    }
}
