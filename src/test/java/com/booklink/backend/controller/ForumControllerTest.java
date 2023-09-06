package com.booklink.backend.controller;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.EditForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.tag.CreateTagDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        createUserAndLogIn("user", "user@email.com", "password");
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
                .tags(new ArrayList<>())
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

        UserDto newUser = createUserAndLogIn("member11", "member@mail.com", "password");
        ResponseEntity<ForumDto> response = restTemplate.exchange(
                baseUrl + "/6/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ForumDto responseForum = ForumDto.builder()
                .id(6L)
                .name("Science of Interstellar")
                .userId(10L)
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .members(new ArrayList<>(List.of(newUser)))
                .tags(new ArrayList<>())
                .build();

        assertEquals(responseForum, response.getBody());
    }

    @Test
    void MemberAlreadyJoinedForumException() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Science of Interstellar")
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .build();
        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        createUserAndLogIn("member11", "member@mail.com", "password");

        restTemplate.exchange(
                baseUrl + "/6/join", HttpMethod.POST, new HttpEntity<>(null), ForumDto.class
        );
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/6/join", HttpMethod.POST, new HttpEntity<>(null), String.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void JoinOwnForumException() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Science of Interstellar")
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("www.1085607313601204255.com")
                .build();
        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/6/join", HttpMethod.POST, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
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


    @Test
    void addTagToForum(){
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Forum")
                .description("description")
                .img("img")
                .build();

        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        ResponseEntity<ForumDto> response = restTemplate.exchange(
                baseUrl + "/6/tag", HttpMethod.POST, new HttpEntity<>(createTagDto), ForumDto.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void forumNotFoundException(){
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/6/tag", HttpMethod.POST, new HttpEntity<>(createTagDto), String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void userNotAdminException(){
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Forum")
                .description("description")
                .img("img")
                .build();

        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .build();
        ResponseEntity<LoginResponseDto> register = restTemplate.postForEntity("/user", createUserDto, LoginResponseDto.class);

        String token = register.getBody().getToken();
        restTemplate.getRestTemplate().setInterceptors(
                List.of((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Bearer " + token);
                    return execution.execute(request, body);
                })
        );


        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/6/tag", HttpMethod.POST, new HttpEntity<>(createTagDto), String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    }

    @Test
    void tagAlreadyAssigned(){
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Forum")
                .description("description")
                .img("img")
                .build();

        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        restTemplate.exchange(
                baseUrl + "/6/tag", HttpMethod.POST, new HttpEntity<>(createTagDto), ForumDto.class
        );

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/6/tag", HttpMethod.POST, new HttpEntity<>(createTagDto), String.class
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void editForum(){

        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Forum")
                .description("description")
                .img("img")
                .build();

        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);


        EditForumDto editForumDto = EditForumDto.builder()
                .name("Don Quijote")
                .description("analisis,discusión y debate acerca de la magistral obra de Miguel de Cervantes ")
                .build();

        EditForumDto editForumDto1 = EditForumDto.builder()
                .name("Don Quijote")
                .description("")
                .build();

        EditForumDto editForumDto2 = EditForumDto.builder()
                .name("D")
                .description("analisis,discusión y debate acerca de la magistral obra de Miguel de Cervantes ")
                .build();


        Long forumId = 6L;

        ResponseEntity<ForumDto> response = restTemplate.exchange(
                baseUrl+"/"+forumId, HttpMethod.PATCH, new HttpEntity<>(editForumDto), ForumDto.class
        );
        ResponseEntity<ForumDto> response1 = restTemplate.exchange(
                baseUrl+"/"+forumId, HttpMethod.PATCH, new HttpEntity<>(editForumDto1), ForumDto.class
        );


        ResponseEntity<ForumDto> response2 = restTemplate.exchange(
                baseUrl+"/"+forumId, HttpMethod.PATCH, new HttpEntity<>(editForumDto2), ForumDto.class
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

    }

    @Test
    void searchForums(){
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Forum")
                .description("description")
                .img("img")
                .build();

        restTemplate.postForEntity(baseUrl, createForumDto, ForumDto.class);

        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        restTemplate.exchange(
                baseUrl + "/6/tag", HttpMethod.POST, new HttpEntity<>(createTagDto), ForumDto.class
        );

        ResponseEntity<List> response = restTemplate.exchange(
                baseUrl + "/search?forumName=Forum&tagIds=1", HttpMethod.GET, null, List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        System.out.println(response.getBody().get(0));


        ResponseEntity<List> response2 = restTemplate.exchange(
                baseUrl + "/search?forumName=Forum", HttpMethod.GET, null, List.class
        );
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(1, response2.getBody().size());

        ResponseEntity<List> response3 = restTemplate.exchange(
                baseUrl + "/search?tagIds=1", HttpMethod.GET, null, List.class
        );

        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertEquals(1, response3.getBody().size());


    }

}
