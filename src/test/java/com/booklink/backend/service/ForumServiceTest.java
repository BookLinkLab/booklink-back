package com.booklink.backend.service;

import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.exception.JoinOwnForumException;
import com.booklink.backend.exception.MemberAlreadyJoinedForumException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
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
        assertFalse(forumService.getAllForums().isEmpty());

        //create forum
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("www.1085607313601204255.com")
                .build();
        ForumDto savedForum = forumService.createForum(createForumDto, 1L);
        List<ForumDto> allForums = forumService.getAllForums();
        assertFalse(allForums.isEmpty());
        assertEquals(6, allForums.size());

        ForumDto myForum = allForums.get(5);
        assertEquals(myForum, savedForum);

        //join user
        UserDto userToJoin = UserDto.builder()
                .id(10L)
                .username("user")
                .email("user@mail.com")
                .build();

        assertTrue(myForum.getMembers().isEmpty());
        forumService.joinForum(myForum.getId(), userToJoin.getId());
        assertFalse(forumService.getAllForums().get(5).getMembers().isEmpty());
    }

    @Test
    void exceptionTest() {
        UserDto userToJoin = UserDto.builder()
                .id(10L)
                .username("user")
                .email("user@mail.com")
                .build();

        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("www.1085607313601204255.com")
                .build();

        ForumDto myForum = forumService.createForum(createForumDto, userToJoin.getId());
        assertThrows(JoinOwnForumException.class, () -> forumService.joinForum(myForum.getId(), userToJoin.getId()));

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("newUser")
                .email("newUser@email.com")
                .password("password")
                .build();
        LoginResponseDto loginResponseDto = userService.registerUser(createUserDto);

        forumService.joinForum(myForum.getId(), loginResponseDto.getUser().getId());
        assertThrows(MemberAlreadyJoinedForumException.class, () -> forumService.joinForum(myForum.getId(), loginResponseDto.getUser().getId()));
    }
}
