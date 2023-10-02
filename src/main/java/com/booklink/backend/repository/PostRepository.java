package com.booklink.backend.repository;

import com.booklink.backend.model.Post;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByForumId(Long forumId);
    Optional<Post> findById(@NonNull Long postId);

}
