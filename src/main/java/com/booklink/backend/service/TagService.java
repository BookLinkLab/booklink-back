package com.booklink.backend.service;

import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.model.Tag;

import java.util.List;

public interface TagService {
    Tag findOrCreateTag(CreateTagDto createTagDto);
    List<Tag> getAllTags();

    void deleteTag(Long id);
}
