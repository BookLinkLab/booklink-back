package com.booklink.backend.controller;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
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

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CommentControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/comment";

    @BeforeEach
    void setup() {
        UserDto user = createUserAndLogIn("user", "user@email.com", "password");
        Long forum = 1L;
        ForumDto forumJoined = joinUserToForum(forum);
        PostDto post = createPost(forum, "This is a test post");
    }

    @Test
    void createCommentTest(){
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
        createUserAndLogIn("notForumMember", "notForumMember@email.com", "password");
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(1L)
                .content("The creator of this comment is not member of the forum")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, createCommentDto, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private UserDto createUserAndLogIn(String username, String email, String password) {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
        ResponseEntity<LoginResponseDto> createUserResponse = restTemplate.postForEntity("/user", createUserDto, LoginResponseDto.class);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
                "/auth", loginRequestDto, LoginResponseDto.class
        );
        String token = Objects.requireNonNull(response.getBody()).getToken();
        restTemplate.getRestTemplate().setInterceptors(
                List.of((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Bearer " + token);
                    return execution.execute(request, body);
                })
        );
        return Objects.requireNonNull(createUserResponse.getBody()).getUser();
    }

    private PostDto createPost(Long forumId, String content) {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(forumId)
                .content(content)
                .build();

        ResponseEntity<PostDto> response = restTemplate.postForEntity("/post", createPostDto, PostDto.class);
        return response.getBody();
    }

    private ForumDto joinUserToForum(Long forumId) {
        ResponseEntity<ForumDto> response = restTemplate.exchange("/forum/%s/join".formatted(forumId), HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);
        return response.getBody();
    }
}
