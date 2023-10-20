package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.post.*;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotOwnerException;
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
public class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private ForumService forumService;
    @Autowired
    private CommentService commentService;

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
        //26 because of InitializationConfig
        assertEquals(26L, postService.getPostsByForumId(1L).get(0).getId());
        CreatePostDto createPostDto2 = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();

        postService.createPost(createPostDto2, 1L);
        assertEquals(2, postService.getPostsByForumId(1L).size());
        assertEquals(27L, postService.getPostsByForumId(1L).get(1).getId());
    }

    @Test
    public void getPostById() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        PostDto postDto = postService.createPost(createPostDto, 1L);

        PostViewDto postViewDto = postService.getPostViewById(1L, postDto.getId());
        assertEquals("This is a test post", postViewDto.getContent());
        assertTrue(postViewDto.getComments().isEmpty());
    }

    @Test
    public void getPostWithCommentById() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        PostDto postDto = postService.createPost(createPostDto, 1L);

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();
        commentService.createComment(createCommentDto, 1L);

        assertEquals("lucia21" ,postService.getPostViewById(1L, postDto.getId()).getComments().get(0).getUsername());
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
        assertEquals(editedPost.isEdited(), true);
    }

    @Test
    public void editPostWithNullContent() {
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

        assertThrows(UserNotOwnerException.class, () -> postService.editPost(postDto.getId(), editPostDto, 2L));
    }

    @Test
    public void deletePost() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);


        PostDto postDto = postService.createPost(createPostDto, 1L);

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(26L)
                .content("This is a test comment")
                .build();
        commentService.createComment(createCommentDto, 1L);


        postService.deletePost(postDto.getId(), 1L);
        assertEquals(0, postService.getPostsByForumId(1L).size());
        assertThrows(NotFoundException.class, () -> commentService.getCommentById(17L));
    }

    @Test
    public void deletePostByUnauthorizedPerson() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);
        postService.createPost(createPostDto, 1L);
        forumService.joinForum(1L, 9L);

        assertThrows(UserNotOwnerException.class, () -> postService.deletePost(1L, 9L));
    }

    @Test
    public void toggleLike() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        PostDto postDto = postService.createPost(createPostDto, 1L);
        assertEquals(0, postDto.getLikes().size());
        //toggle like
        postDto = postService.toggleLike(postDto.getId(), 1L);
        assertEquals(1, postDto.getLikes().size());
        //toggle like when previously liked
        postDto = postService.toggleLike(postDto.getId(), 1L);
        assertEquals(0, postDto.getLikes().size());
    }

    @Test
    public void toggleDislike() {
        CreatePostDto createPostDto = CreatePostDto.builder()
                .forumId(1L)
                .content("This is a test post")
                .build();
        forumService.joinForum(1L, 1L);

        PostDto postDto = postService.createPost(createPostDto, 1L);
        assertEquals(0, postDto.getDislikes().size());
        //toggle dislike
        postDto = postService.toggleDislike(postDto.getId(), 1L);
        assertEquals(1, postDto.getDislikes().size());
        //toggle dislike when previously disliked
        postDto = postService.toggleDislike(postDto.getId(), 1L);
        assertEquals(0, postDto.getDislikes().size());
    }

    @Test
    public void getPostsByForum(){
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .postId(3L)
                .content("This is a test comment")
                .build();
        commentService.createComment(createCommentDto, 3L);
        List<PostInfoDto> posts = postService.getPostsByForumId(3L);
        assertEquals(1, posts.get(0).getCommentsCount());
    }

}
