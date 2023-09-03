package com.booklink.backend.service;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.EditForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.exception.AlreadyAssignedException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotAdminException;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.repository.ForumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ForumServiceTest {
    @Autowired
    private ForumService forumService;
    @Autowired
    private ForumRepository forumRepository;
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

        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        ForumDto forumWithTag = forumService.addTagToForum(6L, 1L, createTagDto);
        assertEquals(1, forumWithTag.getTags().size());

        EditForumDto editForumDto = EditForumDto.builder()
                .name("Don Quijote")
                .description("analisis,discusión y debate acerca de la magistral obra de Miguel de Cervantes ")
                .build();


        Long id = 6L;

        forumService.editForum(id,editForumDto);


        List<ForumDto> allForums1 = forumService.getAllForums();

        assertEquals(6, allForums1.size());
        assertNotEquals(allForums,allForums1);

        Optional<Forum> forumOptional = forumRepository.findById(id);
        Forum forum = forumOptional.orElseThrow(() -> new NotFoundException("%d del foro no encontrado".formatted(id)));
        assertEquals("Don Quijote", forum.getName());
        assertEquals("analisis,discusión y debate acerca de la magistral obra de Miguel de Cervantes ", forum.getDescription());



    }


    @Test
    void forumNotFound(){
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();
        assertThrows(NotFoundException.class, () -> forumService.addTagToForum(6L, 1L, createTagDto));
    }

    @Test
    void userNotForumAdmin(){
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("www.1085607313601204255.com")
                .build();
        forumService.createForum(createForumDto, 6L);
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();
        assertThrows(UserNotAdminException.class, () -> forumService.addTagToForum(6L, 2L, createTagDto));
    }

    @Test
    void tagAlreadyAssigned(){
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("www.1085607313601204255.com")
                .build();
        forumService.createForum(createForumDto, 1L);
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();
        forumService.addTagToForum(6L, 1L, createTagDto);
        assertThrows(AlreadyAssignedException.class, () -> forumService.addTagToForum(6L, 1L, createTagDto));
    }
}
