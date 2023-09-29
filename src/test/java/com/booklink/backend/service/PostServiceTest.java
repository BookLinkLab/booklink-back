package com.booklink.backend.service;


import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.EditPostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.exception.UserNotOwnerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private ForumService forumService;

    @Test
    public void createPost() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();

        forumService.joinForum(1L, 1L);
        PostDto postDto = postService.createPost(createPostDto, 1L);
        assertEquals("This is a test post", postDto.getContent());
    }

    @Test
    public void getPostsByForumId() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        postService.createPost(createPostDto, 1L);
        assertEquals(1, postService.getPostsByForumId(1L).size());
        CreatePostDto createPostDto2 = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();

        postService.createPost(createPostDto2, 1L);
        assertEquals(2, postService.getPostsByForumId(1L).size());
    }

    @Test
    public void editPost() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        PostDto postDto = postService.createPost(createPostDto, 1L);
        assertEquals("This is a test post", postDto.getContent());
        EditPostDto editPostDto = EditPostDto.builder()
                .content("This is an edited post")
                .build();

        PostDto editedPost = postService.editPost(postDto.getId(), editPostDto, 1L);
        assertEquals("This is an edited post", editedPost.getContent());
        assertEquals(postDto.getId(), editedPost.getId());
        assertEquals(editedPost.isEdited(),true);
    }

    @Test
    public void editPostWithNullContent(){
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        PostDto postDto = postService.createPost(createPostDto, 1L);
        assertEquals("This is a test post", postDto.getContent());
        EditPostDto editPostDto = EditPostDto.builder()
                .content(null)
                .build();

        PostDto editedPost = postService.editPost(postDto.getId(), editPostDto, 1L);
        assertEquals("This is a test post", editedPost.getContent());
        assertEquals(postDto.getId(), editedPost.getId());
    }

    @Test
    public void editPostNotOwner() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        PostDto postDto = postService.createPost(createPostDto, 1L);
        assertEquals("This is a test post", postDto.getContent());
        EditPostDto editPostDto = EditPostDto.builder()
                .content("This is an edited post")
                .build();

        assertThrows(UserNotOwnerException.class,() -> postService.editPost(postDto.getId(), editPostDto, 2L));
    }

}
