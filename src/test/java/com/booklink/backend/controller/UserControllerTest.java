package com.booklink.backend.controller;

import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/user";

    @BeforeEach
    void setup() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@email.com")
                .password("password")
                .build();
        restTemplate.postForEntity(baseUrl, createUserDto, UserDto.class);
    }

    @Test
    void registerUser() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("newUser")
                .email("newUser@email.com")
                .password("password")
                .build();

        ResponseEntity<UserDto> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), UserDto.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getAllUsers() {
        ResponseEntity<List<UserDto>> response = this.restTemplate.exchange(
                this.baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void existingEmailException() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("newUser")
                .email("user@email.com")
                .password("password")
                .build();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), String.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void invalidUsernameException() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("d#")
                .email("user@email.com")
                .password("password")
                .build();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void invalidPasswordException() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("newUser")
                .email("user@email.com")
                .password("$")
                .build();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserById(){
        ResponseEntity<UserDto> response = restTemplate.exchange(
                baseUrl + "/1", HttpMethod.GET, null, UserDto.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
