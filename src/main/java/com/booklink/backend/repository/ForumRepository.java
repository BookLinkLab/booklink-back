package com.booklink.backend.repository;

import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Tag;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    Optional<Forum> findById(@NonNull Long id);

    boolean existsByIdAndTagsContaining(Long forumId, Tag tag);

    List<Forum> findAllByNameContainingIgnoreCaseAndTagsIdIsIn(String forumName, List<Long> tagIds);

    List<Forum> findAllByNameContainingIgnoreCase(String forumName);

    List<Forum> findDistinctByNameContainingIgnoreCaseOrTagsNameContainingIgnoreCase(String forumName, String tagName);

    List<Forum> findAllByTagsIdIn(List<Long> tagIds);

    boolean existsByTagsContaining(Tag tag);
}
