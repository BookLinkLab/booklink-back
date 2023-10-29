package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.notification.NotificationViewDto;
import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.exception.UserNotOwnerException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Notification;
import com.booklink.backend.model.NotificationType;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.NotificationRepository;
import com.booklink.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
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
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createPostShouldHaveNotificationTest() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a post test")
                .build();

        forumService.joinForum(1L, 1L);

        activateNotifications(List.of(2L,7L,8L),1L);
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
        activateNotifications(List.of(2L,7L,8L),1L);

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
        Long notificationId = postNotificationToSave.getId();
        User user = userService.getUserEntityById(2L);
        user.setForumNotifications(List.of(notificationId));
        userRepository.save(user);


        assertEquals(1, notificationService.getNotificationsByUserId(2L).size());
        assertEquals("@lucia21 creó una nueva publicación en \"Harry Potter\"!", notificationService.getNotificationsByUserId(2L).get(0).getContent());

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

        Long notificationId2 = commentNotificationToSave.getId();
        user.setForumNotifications(List.of(notificationId, notificationId2));
        userRepository.save(user);


        //now the first notification (get(0)) is the comment notification -> the newest first
        assertEquals(2, notificationService.getNotificationsByUserId(2L).size());
        assertEquals("@tomas creó un nuevo comentario en \"Harry Potter\"!", notificationService.getNotificationsByUserId(2L).get(0).getContent());
    }

    @Test
    public void toggleForumNotification() {
        forumService.joinForum(3L, 2L);
        User user = userService.getUserEntityById(2L);
        List<Long> forumsId = List.of(3L);
        assertEquals(forumsId, user.getForumNotifications());

        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);
        User user2 = userService.getUserEntityById(1L);
        assertEquals(1, user2.getForumNotifications().size());


        forumService.joinForum(1L, 1L);
        notificationService.toggleForumNotification(1L, 1L);
        User user3 = userService.getUserEntityById(1L);
        List<Long> otherForumsId = List.of(11L);
        assertEquals(otherForumsId, user3.getForumNotifications());
    }


    @Test
    public void onlyActivatedForumNotificationsTest(){
        Notification postNotificationToSave = Notification.builder()
                .type(NotificationType.POST)
                .postAuthorId(1L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();

        Notification commentNotificationToSave = Notification.builder()
                .type(NotificationType.COMMENT)
                .postAuthorId(2L)
                .commentAuthorId(2L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();
        notificationRepository.save(postNotificationToSave);


        Long notificationId = postNotificationToSave.getId();
        User user = userService.getUserEntityById(2L);
        user.setForumNotifications(List.of(notificationId));
        userRepository.save(user);


        List<NotificationViewDto> user_notifications = notificationService.getNotificationsByUserId(2L);

        assertEquals(1, user_notifications.size());
        assertEquals("@lucia21 creó una nueva publicación en \"Harry Potter\"!", user_notifications.get(0).getContent());


    }

    private void activateNotifications(List<Long> longs,Long forumId) {
        longs.forEach(aLong -> {
            User user = userService.getUserEntityById(aLong);
            user.setForumNotifications(List.of(forumId));
            userRepository.save(user);
        });
    }


}
