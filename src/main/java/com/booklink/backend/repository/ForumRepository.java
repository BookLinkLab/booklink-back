package com.booklink.backend.repository;

import com.booklink.backend.model.Forum;
import lombok.NonNull;
import com.booklink.backend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    Optional<Forum> findById(@NonNull Long id);

    boolean existsByIdAndTagsContaining(Long forumId, Tag tag);
}
