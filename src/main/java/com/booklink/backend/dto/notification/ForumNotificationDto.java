package com.booklink.backend.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ForumNotificationDto {
    private final Long forumId;
    private final String forumName;
    private final String forumImage;
    private final boolean notification;

    public static ForumNotificationDto from(Long forumId, String forumName, String forumImage, boolean notification) {
        return ForumNotificationDto.builder()
                .forumId(forumId)
                .forumName(forumName)
                .forumImage(forumImage)
                .notification(notification)
                .build();
    }

}
