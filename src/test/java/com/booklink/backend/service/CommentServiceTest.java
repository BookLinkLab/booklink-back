package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.exception.MemberDoesntBelongForumException;
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
        Long postIdToComment = 1L;

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(postIdToComment)
                .content("This is a test comment")
                .build();

        commentService.createComment(createCommentDto, userIdWhoComments);

        CommentDto savedComment = commentService.getCommentById(userIdWhoComments);
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
}
