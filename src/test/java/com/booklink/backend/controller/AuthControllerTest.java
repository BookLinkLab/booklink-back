package com.booklink.backend.controller;


import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;


import static com.booklink.backend.utils.ControllerTestUtils.setOutputStreamingFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void notFoundLogin() {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email@gmail.com")
                .password("password")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth", loginRequestDto, String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void wrongPasswordLogin() {
        setOutputStreamingFalse(restTemplate);

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("email@gmail.com")
                .password("password")
                .build();

        ResponseEntity<UserDto> response = restTemplate.postForEntity(
                "/user", createUserDto, UserDto.class);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email@gmail.com")
                .password("password1")
                .build();

        ResponseEntity<String> response1 = restTemplate.postForEntity(
                "/auth", loginRequestDto, String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());
    }

    @Test
    void invalidLogin() {

        setOutputStreamingFalse(restTemplate);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email")
                .password("password")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth", loginRequestDto, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    void validLogin() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("email@gmail.com")
                .password("password")
                .build();

        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
                "/user", createUserDto, LoginResponseDto.class);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email@gmail.com")
                .password("password")
                .build();

        ResponseEntity<String> response1 = restTemplate.postForEntity(
                "/auth", loginRequestDto, String.class
        );

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(response.getBody().getUser().getEmail(), createUserDto.getEmail());
    }


}


