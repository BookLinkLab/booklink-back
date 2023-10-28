package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.exception.UserNotOwnerException;
import com.booklink.backend.model.Notification;
import com.booklink.backend.model.NotificationType;
import com.booklink.backend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class NotificationServiceTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ForumService forumService;
    @Autowired
    private PostService postService;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void createPostShouldHaveNotificationTest() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a post test")
                .build();

        forumService.joinForum(1L, 1L);
        postService.createPost(createPostDto, 1L);
        List<Notification> notifications = notificationService.getNotificationsEntity();
        assertEquals(3, notifications.size());
    }

    @Test
    void createCommentShouldHaveNotificationTest() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a post test")
                .build();

        forumService.joinForum(1L, 1L);
        PostDto postDto = postService.createPost(createPostDto, 1L);

        Long postId = postDto.getId();

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(postId)
                .content("This is a comment test")
                .build();

        commentService.createComment(createCommentDto, 2L);

        CreateCommentDto createCommentDto1 = CreateCommentDto.builder()
                .postId(postId)
                .content("This is a comment test")
                .build();

        commentService.createComment(createCommentDto1, 8L);


        List<Notification> notifications = notificationService.getNotificationsEntity();
        assertEquals(6, notifications.size());  // 3 notifications for post, 3 notifications for comment
        Notification commentNotification = notifications.get(3);
        assertEquals(2L, commentNotification.getCommentAuthorId());
        assertEquals(1L, commentNotification.getReceiverId());
    }

    @Test
    public void deleteNotification() {
        Notification notificationToSave = Notification.builder()
                .type(NotificationType.POST)
                .postAuthorId(1L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();
        notificationRepository.save(notificationToSave);

        assertEquals(1, notificationService.getNotificationsEntity().size());
        notificationService.deleteNotification(1L, 2L);
        List<Notification> notifications = notificationService.getNotificationsEntity();
        assertEquals(0, notifications.size());
    }

    @Test
    public void unauthorizedUserDeletesNotification() {
        Notification notificationToSave = Notification.builder()
                .type(NotificationType.POST)
                .postAuthorId(1L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();
        notificationRepository.save(notificationToSave);

        assertEquals(1, notificationService.getNotificationsEntity().size());
        assertThrows(UserNotOwnerException.class, () -> notificationService.deleteNotification(1L, 3L));
    }

    @Test
    public void getNotificationsTest() {
        Notification postNotificationToSave = Notification.builder()
                .type(NotificationType.POST)
                .postAuthorId(1L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();
        notificationRepository.save(postNotificationToSave);

        assertEquals(1, notificationService.getNotificationsByUserId(2L).size());
        assertEquals("@lucia21 creó una nueva publicación en Harry Potter!", notificationService.getNotificationsByUserId(2L).get(0).getContent());

        Notification commentNotificationToSave = Notification.builder()
                .type(NotificationType.COMMENT)
                .postAuthorId(2L)
                .commentAuthorId(2L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();
        notificationRepository.save(commentNotificationToSave);

        //now the first notification (get(0)) is the comment notification -> the newest first
        assertEquals(2, notificationService.getNotificationsByUserId(2L).size());
        assertEquals("@tomas creó un nuevo comentario en Harry Potter!", notificationService.getNotificationsByUserId(2L).get(0).getContent());
    }
}
