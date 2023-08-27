package com.booklink.backend.service;


import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.WrongCredentialsException;
import com.booklink.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;


    @Test
    void LoginInvalidUser() {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email@gmail.com")
                .password("password")
                .build();

        assertThrows(NotFoundException.class, () -> authService.login(loginRequestDto));
    }

    @Test
    void LoginInvalidPassword() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("email@gmail.com")
                .password("password")
                .build();

        userService.registerUser(createUserDto);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email@gmail.com")
                .password("password1")
                .build();

        assertThrows(WrongCredentialsException.class, () -> authService.login(loginRequestDto));

    }

    @Test
    void LoginValidUser() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("email@gmail.com")
                .password("password")
                .build();

        userService.registerUser(createUserDto);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email@gmail.com")
                .password("password")
                .build();

        LoginResponseDto login = authService.login(loginRequestDto);
        assertTrue(login.getToken().length() > 0);
    }

}

