package com.booklink.backend.controller;

import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.post.CreatePostDto;
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

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class PostControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ControllerTestUtils utils;

    private final String baseUrl = "/post";

    @BeforeEach
    void setup() {
        UserDto user = utils.createUserAndLogIn("user", "user@email.com", "password", restTemplate);
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

        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/forum/1", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());

        ResponseEntity<String> response2 = restTemplate.getForEntity(baseUrl + "/24543", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void getPostById() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        restTemplate.exchange("/forum/1/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);
        PostDto postDto = restTemplate.postForEntity(baseUrl, createPostDto, PostDto.class).getBody();

        ResponseEntity<PostDto> response = restTemplate.getForEntity(baseUrl + "/" + postDto.getId(), PostDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("This is a test post", Objects.requireNonNull(response.getBody()).getContent());

        ResponseEntity<String> response2 = restTemplate.getForEntity(baseUrl + "/24543", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void deletePost() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();

        restTemplate.exchange("/forum/1/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);
        PostDto postDto = restTemplate.postForEntity(baseUrl, createPostDto, PostDto.class).getBody();

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + postDto.getId(), HttpMethod.DELETE, new HttpEntity<>(null), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("La publicación fue eliminada con éxito", response.getBody());
    }

    @Test
    public void editPost() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        restTemplate.exchange("/forum/1/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);

        PostDto postDto = restTemplate.postForEntity(baseUrl, createPostDto, PostDto.class).getBody();
        assertEquals("This is a test post", postDto.getContent());

        CreatePostDto createPostDto2 = CreatePostDto.builder()
                .forumId(1L)
                .content("This is an edited test post")
                .build();

        ResponseEntity<PostDto> postDto1 = restTemplate.exchange(baseUrl + "/" + postDto.getId(), HttpMethod.PATCH, new HttpEntity<>(createPostDto2), PostDto.class);
        assertEquals(HttpStatus.OK, postDto1.getStatusCode());
        assertEquals("This is an edited test post", Objects.requireNonNull(postDto1.getBody()).getContent());

        ResponseEntity<String> response2 = restTemplate.exchange(baseUrl + "/24543", HttpMethod.PATCH, new HttpEntity<>(createPostDto2), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());

        CreatePostDto createPostDto3 = CreatePostDto.builder()
                .forumId(1L)
                .build();

        ResponseEntity<PostDto> response3 = restTemplate.exchange(baseUrl + "/" + postDto.getId(), HttpMethod.PATCH, new HttpEntity<>(createPostDto3), PostDto.class);
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertEquals("This is an edited test post", Objects.requireNonNull(response3.getBody().getContent()));
    }

    @Test
    public void toggleLike() {
        utils.createPost(1L, "This is a test post", restTemplate);
        ResponseEntity<PostDto> response = restTemplate.exchange(baseUrl + "/1/toggle-like", HttpMethod.POST, new HttpEntity<>(null), PostDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getLikes().size());
        assertEquals(10L, Objects.requireNonNull(response.getBody()).getLikes().get(0));
    }
}
