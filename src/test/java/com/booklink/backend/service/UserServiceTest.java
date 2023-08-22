package com.booklink.backend.service;

import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void happyPathTest(){

        assertTrue(userService.getAllUsers().isEmpty());

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@mail.com")
                .password("password")
                .build();

        UserDto savedUser = userService.registerUser(createUserDto);

        List<UserResponseDto> allUsers = userService.getAllUsers();
        assertFalse(allUsers.isEmpty());
        assertEquals(1, allUsers.size());

        UserDto myUser = allUsers.get(0).getUserDto();
        assertEquals(myUser, savedUser);
    }

    @Test
    void unhappyPathTest(){
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@email.com")
                .password("password")
                .build();

        userService.registerUser(createUserDto);


        assertEquals(1, userService.getAllUsers().size());

        CreateUserDto existingUsernameDto = CreateUserDto.builder()
                .username("user")
                .email("sameuser@email.com")
                .password("password")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userService.registerUser(existingUsernameDto));
        assertEquals(1, userService.getAllUsers().size());

        CreateUserDto existingEmailDto = CreateUserDto.builder()
                .username("sameuser")
                .email("user@email.com")
                .password("password")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userService.registerUser(existingEmailDto));
        assertEquals(1, userService.getAllUsers().size());
    }
}
