package com.booklink.backend.controller;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.utils.ControllerTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CommentControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ControllerTestUtils utils;

    private final String baseUrl = "/comment";

    @BeforeEach
    void setup() {
        UserDto user = utils.createUserAndLogIn("user", "user@email.com", "password", restTemplate);
        ForumDto forumJoined = utils.joinUserToForum(1L, restTemplate);
        PostDto post = utils.createPost(1L, "This is a test post", restTemplate);
    }

    @Test
    void createCommentTest() {
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(1L)
                .content("This is a test comment")
                .build();

        ResponseEntity<CommentDto> response = restTemplate.postForEntity(baseUrl, createCommentDto, CommentDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Objects.requireNonNull(response.getBody()).getContent(), createCommentDto.getContent());
    }

    @Test
    void createCommentWhenNotMemberTest() {
        utils.createUserAndLogIn("notForumMember", "notForumMember@email.com", "password", restTemplate);
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(1L)
                .content("The creator of this comment is not member of the forum")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, createCommentDto, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
