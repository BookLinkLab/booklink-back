package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.comment.EditCommentDto;
import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.exception.MemberDoesntBelongForumException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotOwnerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private ForumService forumService;
    @Autowired
    private PostService postService;

    @BeforeEach
    public void setup() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a post test")
                .build();

        forumService.joinForum(1L, 1L);
        postService.createPost(createPostDto, 1L);
    }

    @Test
    void createCommentTest() {
        Long userIdWhoComments = 1L;
        Long postIdToComment = 26L;

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(postIdToComment)
                .content("This is a test comment")
                .build();

        commentService.createComment(createCommentDto, userIdWhoComments);

        CommentDto savedComment = commentService.getCommentById(17L);
        assertEquals(createCommentDto.getContent(), savedComment.getContent());
    }

    @Test
    void createCommentWhenNotMemberTest() {
        Long userIdWhoComments = 3L;
        Long postIdToComment = 1L;

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(postIdToComment)
                .content("This is a test comment")
                .build();

        assertThrows(MemberDoesntBelongForumException.class, () -> commentService.createComment(createCommentDto, userIdWhoComments));
    }

    @Test
    void delteCommenteTest(){
        Long userIdWhoComments = 1L;
        Long postIdToComment = 26L;

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(postIdToComment)
                .content("This is a test comment")
                .build();

        CreateCommentDto createCommentDto1 = CreateCommentDto.builder()
                .postId(postIdToComment)
                .content("This is a second test comment")
                .build();

        commentService.createComment(createCommentDto, userIdWhoComments);
        commentService.createComment(createCommentDto1, userIdWhoComments);
        commentService.deleteComment(17L, userIdWhoComments);

        assertThrows(NotFoundException.class, () -> commentService.getCommentById(17L));

        commentService.createComment(createCommentDto, userIdWhoComments);
        //ahora probando que lo elimine el dueño del foro
        commentService.deleteComment(18L, 2L);
        commentService.createComment(createCommentDto1, userIdWhoComments);
        assertThrows(NotFoundException.class, () -> commentService.getCommentById(18L));

    }

    @Test
    void unauthorizedUserDeltesComment(){
        Long userIdWhoComments = 1L;
        Long postIdToComment = 26L;

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(postIdToComment)
                .content("This is a test comment")
                .build();

        commentService.createComment(createCommentDto, userIdWhoComments);
        assertThrows(UserNotOwnerException.class, () -> commentService.deleteComment(17L, 8L));
    }

    @Test
    void editCommentTest(){
        Long userIdWhoComments = 1L;
        Long postIdToComment = 26L;

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(postIdToComment)
                .content("This is a test comment")
                .build();

        commentService.createComment(createCommentDto, userIdWhoComments);
        CommentDto commentDto = commentService.getCommentById(17L);
        assertEquals(createCommentDto.getContent(), commentDto.getContent());
        EditCommentDto editCommentDto = new EditCommentDto("This is an edited comment");

        CommentDto editedComment = commentService.editComment(17L, editCommentDto, userIdWhoComments);
        assertEquals(editCommentDto.getContent(), editedComment.getContent());

        assertThrows(UserNotOwnerException.class, () -> commentService.editComment(17L, editCommentDto, 8L));
    }


    @Test
    public void toggleLike() {
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();
        CommentDto commentDto = commentService.createComment(createCommentDto, 1L);

        assertEquals(0, commentDto.getLikes().size());

        CommentDto likedComment = commentService.toggleLike(commentDto.getId(), 1L);
        assertEquals(1, likedComment.getLikes().size());

        likedComment = commentService.toggleLike(commentDto.getId(), 1L);
        assertEquals(0, likedComment.getLikes().size());
    }

    @Test
    public void toggleDislike() {
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();
        CommentDto commentDto = commentService.createComment(createCommentDto, 1L);

        assertEquals(0, commentDto.getDislikes().size());

        CommentDto dislikedComment = commentService.toggleDislike(commentDto.getId(), 1L);
        assertEquals(1, dislikedComment.getDislikes().size());

        dislikedComment = commentService.toggleDislike(commentDto.getId(), 1L);
        assertEquals(0, dislikedComment.getDislikes().size());

        CommentDto likedComment = commentService.toggleLike(commentDto.getId(), 1L);
        assertEquals(1, likedComment.getLikes().size());

        dislikedComment = commentService.toggleDislike(commentDto.getId(), 1L);
        assertEquals(1, dislikedComment.getDislikes().size());
        assertEquals(0, dislikedComment.getLikes().size());
    }


}
