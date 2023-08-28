package com.booklink.backend.service;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ForumServiceTest {
    @Autowired
    private ForumService forumService;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@mail.com")
                .password("password")
                .build();
        userService.registerUser(createUserDto);
    }

    @Test
    void happyPathTest() {
        UserDto forumCreator = userService.getAllUsers().get(0);
        //assertTrue(forumService.getAllForums().isEmpty());

        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .userId(forumCreator.getId())
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("www.1085607313601204255.com")
                .build();
        ForumDto savedForum = forumService.createForum(createForumDto);
        List<ForumDto> allForums = forumService.getAllForums();
        assertFalse(allForums.isEmpty());
        assertEquals(1, allForums.size());

        ForumDto myForum = allForums.get(0);
        assertEquals(myForum, savedForum);
    }

    @Test
    void exceptionTest() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .userId(-1L)
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("www.1085607313601204255.com")
                .build();
        assertThrows(NotFoundException.class, () -> forumService.createForum(createForumDto));
    }
}
