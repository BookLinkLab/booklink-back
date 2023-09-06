package com.booklink.backend.repository;

import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    boolean existsByIdAndTagsContaining(Long forumId, Tag tag);

    List<Forum> findAllByNameContainingIgnoreCaseAndTagsIdIsIn(String forumName, List<Long> tagIds);

    List<Forum> findAllByNameContainingIgnoreCase(String forumName);

    List<Forum> findAllByTagsIdIn(List<Long> tagIds);
}
