package com.booklink.backend.controller;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.exception.MemberAlreadyJoinedForumException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.User;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ForumControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/forum";

    @BeforeEach
    void setup() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@email.com")
                .password("password")
                .build();
        restTemplate.postForEntity("/user", createUserDto, UserDto.class);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("user@email.com")
                .password("password")
                .build();
        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
                "/auth", loginRequestDto, LoginResponseDto.class
        );
        String token = response.getBody().getToken();
        restTemplate.getRestTemplate().setInterceptors(
                List.of((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Bearer " + token);
                    return execution.execute(request, body);
                })
        );
    }

    @Test
    void createForum() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Science of Interstellar")
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .build();

        ResponseEntity<ForumDto> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createForumDto), ForumDto.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ForumDto responseForum = ForumDto.builder()
                .id(6L)
                .name("Science of Interstellar")
                .userId(10L)
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .members(new ArrayList<>())
                .build();
        assertEquals(response.getBody(), responseForum);
    }

    @Test
    void JoinForum() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Science of Interstellar")
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .build();

        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        ResponseEntity<ForumDto> response = restTemplate.exchange(
                baseUrl + "/6/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDto loggedUser = UserDto.builder()
                .id(10L)
                .username("user")
                .email("user@email.com")
                .build();

        ForumDto responseForum = ForumDto.builder()
                .id(6L)
                .name("Science of Interstellar")
                .userId(10L)
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .members(new ArrayList<>(List.of(loggedUser)))
                .build();

        assertEquals(responseForum, response.getBody());
    }

    @Test
    void JoinForumException() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Science of Interstellar")
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .build();

        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        restTemplate.exchange(
                baseUrl + "/6/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/6/join", HttpMethod.POST, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
