package com.booklink.backend.service;

import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserProfileDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.User;
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
    private ForumService forumService;
    @Autowired
    private UserService userService;

    @Test
    void happyPathTest() {

        assertEquals(9, userService.getAllUsers().size());

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@mail.com")
                .password("password")
                .build();
        LoginResponseDto registerDto = userService.registerUser(createUserDto);
        UserDto savedUser = registerDto.getUser();
        assertFalse(registerDto.getToken().isEmpty());

        List<UserDto> allUsers = userService.getAllUsers();
        assertFalse(allUsers.isEmpty());
        assertEquals(10, allUsers.size());

        UserDto myUser = allUsers.get(9);

        assertEquals(myUser, savedUser);


        UserProfileDto userDto = userService.getUserById(savedUser.getId(),1L);
        assertEquals(savedUser.getId(), userDto.getId());

        assertTrue(userDto.getForumsJoined().isEmpty());
        assertTrue(userDto.getForumsCreated().isEmpty());

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

        assertThrows(NotFoundException.class, () -> userService.getUserById(0L,1L));
    }

    @Test
    void forumsInCommon(){

        //foro1,2 y 4 -> usuarios_ids: 2,7,8
        //foro3 y 5 -> usuarios_ids: 3,8,9


        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("..")
                .build();
        forumService.createForum(createForumDto, 3L);

        User user1 = userService.getUserEntityById(2L);
        User user2 = userService.getUserEntityById(3L);


       UserProfileDto userSearched = userService.getUserById(user1.getId(), user2.getId());

       assertEquals(1, userSearched.getForumsCreated().size());
       assertEquals(3, userSearched.getForumsJoined().size());



        List<ForumDto> forums1 = userSearched.getForumsCreated();
        forums1.addAll(userSearched.getForumsJoined());

        for (ForumDto forum : forums1) {
            assertFalse(forum.isSearcherIsMember()); // testeando que el userId 3 no es miembro de ningun foro del userId 2
        }

        forumService.joinForum(4L, user2.getId());
        UserProfileDto userSearchedAgain = userService.getUserById(user1.getId(), user2.getId());

        List<ForumDto> forumsAgain = userSearchedAgain.getForumsCreated();
        forumsAgain.addAll(userSearchedAgain.getForumsJoined());

        assertTrue(forumsAgain.get(3).isSearcherIsMember()); //despues de que el userid 3 se una al foro 4 del userid 2, se testea que ahora si es miembro de ese foro




        UserProfileDto userSearched2 = userService.getUserById(2L, 8L);
        List<ForumDto> forums2 = userSearched2.getForumsCreated();


        for (ForumDto forum : forums2) {
            assertTrue(forum.isSearcherIsMember()); // testeando que el user 8 es miembro de todos los foros del user 2
        }


        //testeando que el user 8 no es miembro del foro 5 del que el user 3 creo (hecho mas arriba en el test)
        UserProfileDto userSearched3 = userService.getUserById(3L, 8L);

        List<ForumDto> forums3 = userSearched3.getForumsCreated();
        forums3.addAll(userSearched3.getForumsJoined());

        for (ForumDto forum : forums2) {
            if(forum.getId() == 6L){assertFalse(forum.isSearcherIsMember());}
            else{
                assertTrue(forum.isSearcherIsMember());
            }
        }


    }

}
