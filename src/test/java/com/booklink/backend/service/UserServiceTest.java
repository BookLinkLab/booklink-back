package com.booklink.backend.service;

import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.exception.NotFoundException;
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
    void happyPathTest() {
        assertTrue(userService.getAllUsers().isEmpty());

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@mail.com")
                .password("password")
                .build();
        UserDto savedUser = userService.registerUser(createUserDto);

        List<UserDto> allUsers = userService.getAllUsers();
        assertFalse(allUsers.isEmpty());
        assertEquals(1, allUsers.size());

        UserDto myUser = allUsers.get(0);

        assertEquals(myUser, savedUser);


        UserDto userDto = userService.getUserById(savedUser.getId());
        assertEquals(savedUser, userDto);
    }

    @Test
    void exceptionTest() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@mail.com")
                .password("password")
                .build();
        userService.registerUser(createUserDto);

        CreateUserDto existingUsernameDto = CreateUserDto.builder()
                .username("user")
                .email("existingUsernameDto@mail.com")
                .password("password")
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> userService.registerUser(existingUsernameDto));

        CreateUserDto existingEmailDto = CreateUserDto.builder()
                .username("existingEmailDto")
                .email("user@mail.com")
                .password("password")
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> userService.registerUser(existingEmailDto));
    }

    @Test
    void exceptionTest(){
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }
}
