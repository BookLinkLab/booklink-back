package com.booklink.backend.utils;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ControllerTestUtils {
    @Autowired
    private TestRestTemplate restTemplate;

    public static void setOutputStreamingFalse(TestRestTemplate restTemplate) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);
    }

    public UserDto createUserAndLogIn(String username, String email, String password) {
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

    public PostDto createPost(Long forumId, String content) {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(forumId)
                .content(content)
                .build();

        ResponseEntity<PostDto> response = restTemplate.postForEntity("/post", createPostDto, PostDto.class);
        return response.getBody();
    }

    public ForumDto joinUserToForum(Long forumId) {
        ResponseEntity<ForumDto> response = restTemplate.exchange("/forum/%s/join".formatted(forumId), HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);
        return response.getBody();
    }
}
