package com.booklink.backend.repository;

import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    boolean existsByIdAndTagsContaining(Long forumId, Tag tag);
}
