package com.booklink.backend.controller;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.comment.EditCommentDto;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                .postId(26L)
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

    @Test
    void deleteCommentTest() {
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();

        ResponseEntity<CommentDto> response = restTemplate.postForEntity(baseUrl, createCommentDto, CommentDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ResponseEntity<String> deleteResponse = restTemplate.exchange(baseUrl + "/" + response.getBody().getId(), HttpMethod.DELETE,new HttpEntity<>(null), String.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    @Test
    void editCommentTest() {
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();

        ResponseEntity<CommentDto> response = restTemplate.postForEntity(baseUrl, createCommentDto, CommentDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ResponseEntity<CommentDto> editResponse = restTemplate.exchange(baseUrl + "/" + response.getBody().getId(), HttpMethod.PATCH, new HttpEntity<>(new EditCommentDto("This is an edited comment")), CommentDto.class);
        assertEquals(HttpStatus.OK, editResponse.getStatusCode());
        assertEquals(Objects.requireNonNull(editResponse.getBody()).getContent(), "This is an edited comment");
    }

    @Test
    void editCommentWhenNotOwnerTest() {
        EditCommentDto editCommentDto = new EditCommentDto("This is an edited comment");
        ResponseEntity<?> editResponse = restTemplate.exchange(baseUrl + "/" + 1L, HttpMethod.PATCH, new HttpEntity<>(editCommentDto), String.class);
        assertEquals("No tienes permiso para editar este comentario", editResponse.getBody());
        assertEquals(HttpStatus.FORBIDDEN, editResponse.getStatusCode());
    }

    @Test
    void toggleLike(){
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();
        ResponseEntity<CommentDto> commentDto = restTemplate.postForEntity(baseUrl, createCommentDto, CommentDto.class);

        ResponseEntity<CommentDto> response = restTemplate.exchange(baseUrl + "/" + commentDto.getBody().getId() + "/toggle-like", HttpMethod.POST, new HttpEntity<>(null), CommentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getLikes().size());
        assertEquals(10L, response.getBody().getLikes().get(0));
    }

    @Test
    void toggleDislike(){
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();
        ResponseEntity<CommentDto> commentDto = restTemplate.postForEntity(baseUrl, createCommentDto, CommentDto.class);

        ResponseEntity<CommentDto> response = restTemplate.exchange(baseUrl + "/" + commentDto.getBody().getId() + "/toggle-dislike", HttpMethod.POST, new HttpEntity<>(null), CommentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getDislikes().size());
        assertEquals(10L, response.getBody().getDislikes().get(0));
    }

}
