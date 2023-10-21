package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.model.Notification;
import com.booklink.backend.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
