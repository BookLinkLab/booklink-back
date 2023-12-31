package com.booklink.backend.service;

import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.*;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.exception.*;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Tag;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.ForumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired
    private TagService tagService;

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
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        ForumDto savedForum = forumService.createForum(createForumDto, 1L);
        List<ForumDto> allForums = forumService.getAllForums();
        assertFalse(allForums.isEmpty());
        assertEquals(11, allForums.size());

        ForumDto myForum = allForums.get(10);
        assertNotEquals(savedForum , myForum);

        //join user
        UserDto userToJoin = UserDto.builder()
                .id(10L)
                .username("user")
                .email("user@mail.com")
                .build();

        assertTrue(myForum.getMembers().isEmpty());
        forumService.joinForum(myForum.getId(), userToJoin.getId());
        assertFalse(forumService.getAllForums().get(10).getMembers().isEmpty());

        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        ForumDto forumWithTag = forumService.addTagToForum(11L, 1L, createTagDto);
        assertEquals(1, forumWithTag.getTags().size());
        assertEquals(9, tagService.getAllTags().size());

        CreateTagDto tag2 = CreateTagDto.builder()
                .name("Tag2")
                .build();

        EditForumDto editForumDto = EditForumDto.builder()
                .name("Don Quijote")
                .description("analisis,discusión y debate acerca de la magistral obra de Miguel de Cervantes ")
                .tags(List.of(tag2))
                .build();


        Long id = 11L;
        Long adminUserId = 1L;


        ForumDto editedForum = forumService.editForum(id, adminUserId, editForumDto);


        List<ForumDto> allForums1 = forumService.getAllForums();
        List<Tag> allTags = tagService.getAllTags();

        assertEquals(9, allTags.size());
        assertEquals("Action", allTags.get(0).getName());
        assertEquals(1, editedForum.getTags().size());

        assertEquals(11, allForums1.size());
        assertNotEquals(allForums, allForums1);
        assertNotEquals(allForums, allForums1);
        assertEquals(1, forumWithTag.getTags().size());

        Optional<Forum> forumOptional = forumRepository.findById(id);
        Forum forum = forumOptional.orElseThrow(() -> new NotFoundException("%d del foro no encontrado".formatted(id)));
        assertEquals("Don Quijote", forum.getName());
        assertEquals("analisis,discusión y debate acerca de la magistral obra de Miguel de Cervantes ", forum.getDescription());


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
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
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

    @Test
    void invalidImage() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("invalid")
                .tags(new ArrayList<>())
                .build();
        assertThrows(InvalidImageException.class, () -> forumService.createForum(createForumDto, 1L));
    }


    @Test
    void forumNotFound() {
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();
        assertThrows(NotFoundException.class, () -> forumService.addTagToForum(11L, 1L, createTagDto));
    }

    @Test
    void userNotForumAdmin() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 6L);
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();
        assertThrows(UserNotAdminException.class, () -> forumService.addTagToForum(11L, 2L, createTagDto));
    }

    @Test
    void notAdminEdit() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        EditForumDto editForumDto = EditForumDto.builder()
                .name("Don Quijote")
                .description("analisis,discusión y debate acerca de la magistral obra de Miguel de Cervantes ")
                .build();

        Long nonAdminUserId = 3L;
        Long forumId = 6L;

        assertThrows(UserNotAdminException.class, () -> forumService.editForum(forumId, nonAdminUserId, editForumDto));


    }

    @Test
    void tagAlreadyAssigned() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();
        forumService.addTagToForum(11L, 1L, createTagDto);
        assertThrows(AlreadyAssignedException.class, () -> forumService.addTagToForum(11L, 1L, createTagDto));
    }

    @Test
    void searchForumsByName() {
        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Fiction")
                .build();
        forumService.addTagToForum(11L, 1L, createTagDto);
        List<ForumViewDto> forums = forumService.searchForums("LORD OF THE RINGS",1L);
        assertEquals(1, forums.size());

        List<ForumViewDto> forums2 = forumService.searchForums("fiction",1L);

        assertEquals(8, forums2.size());

        List<ForumViewDto> forums3 = forumService.searchForums("Fiction horror",1L);
        assertEquals(1, forums3.size());

    }

    @Test
    void searchForumsByNameNull() {

        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        List<ForumViewDto> forums3 = forumService.searchForums(null, 1L);
        assertEquals(11, forums3.size());
        assertEquals(forumName, forums3.get(10).getName());

    }


    @Test
    void deleteForum() {
        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);
        forumService.deleteForum(11L, 1L);
        List<ForumDto> allForums = forumService.getAllForums();
        assertEquals(10, allForums.size());
    }

    @Test
    void deleteForumNotFound() {
        assertThrows(NotFoundException.class, () -> forumService.deleteForum(11L, 1L));
    }

    @Test
    void deleteForumAndCheckTags() {
        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);
        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Fiction")
                .build();

        forumService.addTagToForum(11L, 1L, createTagDto);
        forumService.deleteForum(11L, 1L);
        List<ForumDto> allForums = forumService.getAllForums();
        assertEquals(10, allForums.size());
        assertEquals(2, allForums.get(9).getTags().size());
    }

    @Test
    void leaveForumTest() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        List<ForumDto> allForums = forumService.getAllForums();
        ForumDto myForum = allForums.get(5);

        UserDto user = UserDto.builder()
                .id(10L)
                .username("user")
                .email("user@mail.com")
                .build();

        forumService.joinForum(myForum.getId(), user.getId());
        forumService.leaveForum(myForum.getId(), user.getId());
    }

    @Test
    void leaveForumWhenUserIsNotMember() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Interstellar")
                .description("Welcome to the subreddit dedicated to the movie Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        List<ForumDto> allForums = forumService.getAllForums();
        ForumDto myForum = allForums.get(5);

        UserDto loggedUser = UserDto.builder()
                .id(10L)
                .username("user")
                .email("user@mail.com")
                .build();

        assertThrows(MemberDoesntBelongForumException.class,
                () -> forumService.leaveForum(myForum.getId(), loggedUser.getId()));
    }

    @Test
    void leaveForumNotFound() {
        UserDto loggedUser = UserDto.builder()
                .id(10L)
                .username("user")
                .email("user@mail.com")
                .build();

        assertThrows(NotFoundException.class,
                () -> forumService.leaveForum(-1L, loggedUser.getId()));
    }

    @Test
    void getForumById() {

        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        Long forumId = 11L;

        User user = userService.getUserEntityById(1L);

        ForumGetDto forumGetDto = forumService.getForumById(forumId,5L);

        assertEquals(forumName, forumGetDto.getTitle());
        assertEquals("Fans of LOTR", forumGetDto.getDescription());
        assertEquals(0, forumGetDto.getTags().size());
        assertEquals(1, forumGetDto.getMembers());
        assertEquals(user.getId(), forumGetDto.getOwnerId());
        assertEquals("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200", forumGetDto.getImg());

    }

    @Test
    void getForumByWrongId() {
        Long forumId = 11L;
        assertThrows(NotFoundException.class, () -> forumService.getForumById(forumId,5L));
    }

    @Test
    void getForumMember(){
        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        Long forumId = 11L;

        User user = userService.getUserEntityById(2L);
        forumService.joinForum(forumId, user.getId());

        ForumGetDto forumGetDto = forumService.getForumById(forumId,1L);
        ForumGetDto forumGetDto1 = forumService.getForumById(forumId,2L);
        ForumGetDto forumGetDto2 = forumService.getForumById(forumId,3L);

        assertTrue(forumGetDto.isSearcherIsMember());
        assertTrue(forumGetDto1.isSearcherIsMember());
        assertFalse(forumGetDto2.isSearcherIsMember());
    }

    @Test
    void searchForumsMembers(){
        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        Long forumId = 11L;

        User user = userService.getUserEntityById(2L);
        forumService.joinForum(forumId, user.getId());


        List<ForumViewDto> forumViewDtos = forumService.searchForums(null, 2L);
        List<ForumViewDto> forumViewDtos1 = forumService.searchForums("Lord of the Rings", 2L);

        assertEquals(11, forumViewDtos.size());
        List<Long> ids = new ArrayList<>(List.of(1L, 2L, 4L, 6L, 7L, 9L, 11L));
        for (ForumViewDto forumViewDto : forumViewDtos) {
            if(ids.contains(forumViewDto.getId())){
                assertTrue(forumViewDto.isSearcherIsMember());}
            else{
                assertFalse(forumViewDto.isSearcherIsMember());
            }
        }

        assertEquals(1, forumViewDtos1.size());
        assertTrue(forumViewDtos1.get(0).isSearcherIsMember());


    }




    @Test
    void editForumWithSomeFields() {
        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        EditForumDto editForumDto = EditForumDto.builder()
                .name("Don Quijote")
                .build();

        Long id = 11L;
        Long adminUserId = 1L;

        ForumDto editedForum = forumService.editForum(id, adminUserId ,editForumDto);

        assertEquals("Don Quijote", editedForum.getName());
        assertEquals(0, editedForum.getTags().size());
        assertEquals("Fans of LOTR", editedForum.getDescription());
        assertEquals("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200", editedForum.getImg());

    }
    @Test
    void createForumWithTagsTest() {
        List<String> tagsName = Arrays.asList("science", "space", "starship");
        List<CreateTagDto> createTagsDto = tagsName.stream()
                .map(tagName -> CreateTagDto.builder().name(tagName).build())
                .toList();

        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Science of Interstellar")
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(createTagsDto)
                .build();

        ForumDto savedForum = forumService.createForum(createForumDto, 1L);

        assertEquals(savedForum.getTags(), forumService.getAllForums().get(10).getTags());
    }

    @Test
    void createForumWithoutTagsTest() {
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name("Science of Interstellar")
                .description("Welcome to the forum dedicated to the book The Science of Interstellar!")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        ForumDto savedForum = forumService.createForum(createForumDto, 1L);

        assertEquals(savedForum.getTags(), forumService.getAllForums().get(10).getTags());
    }

    @Test
    void forumMembersAmountTest(){
        String forumName = "Lord of the Rings";
        CreateForumDto createForumDto = CreateForumDto.builder()
                .name(forumName)
                .description("Fans of LOTR")
                .img("https://imageio.forbes.com/specials-images/imageserve/5f85be4ed0acaafe77436710/0x0.jpg?format=jpg&width=1200")
                .tags(new ArrayList<>())
                .build();
        forumService.createForum(createForumDto, 1L);

        Long forumId = 11L;
        Forum forum = forumService.getForumEntityById(forumId);

        //testeando que el creador del foro sea miembro
        assertEquals(1, forum.getMembersAmount());

        User user1 = userService.getUserEntityById(2L);
        User user2 = userService.getUserEntityById(3L);
        User user3 = userService.getUserEntityById(4L);

        //testeando que se sumen los miembros
        forumService.joinForum(forumId, user1.getId());
        assertEquals(2, forumService.getForumEntityById(11L).getMembersAmount());
        forumService.joinForum(forumId, user2.getId());
        assertEquals(3, forumService.getForumEntityById(11L).getMembersAmount());
        forumService.joinForum(forumId, user3.getId());
        assertEquals(4, forumService.getForumEntityById(11L).getMembersAmount());

        //testeando que se resten los miembros
        forumService.leaveForum(forumId, user1.getId());
        assertEquals(3, forumService.getForumEntityById(11L).getMembersAmount());
        forumService.leaveForum(forumId, user2.getId());
        assertEquals(2, forumService.getForumEntityById(11L).getMembersAmount());
        forumService.leaveForum(forumId, user3.getId());
        assertEquals(1, forumService.getForumEntityById(11L).getMembersAmount());



    }




}
