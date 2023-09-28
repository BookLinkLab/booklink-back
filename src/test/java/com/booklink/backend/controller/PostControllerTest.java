package com.booklink.backend.controller;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
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
public class PostControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/post";

    @BeforeEach
    void setup() {
        createUserAndLogIn("user", "user@email.com", "password");
    }

    @Test
    public void createPost() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();

        restTemplate.exchange("/forum/1/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);

        ResponseEntity<PostDto> response = restTemplate.postForEntity(baseUrl, createPostDto, PostDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        CreatePostDto createPostDto2 = CreatePostDto.builder()
                .forumId(24543L)
                .content("This is a test post")
                .build();

        ResponseEntity<String> response2 = restTemplate.postForEntity(baseUrl, createPostDto2, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void getPostsByForumId() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        restTemplate.exchange("/forum/1/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);
        restTemplate.postForEntity(baseUrl, createPostDto, PostDto.class);

        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/1", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());

        ResponseEntity<String> response2 = restTemplate.getForEntity(baseUrl + "/24543", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
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
}
