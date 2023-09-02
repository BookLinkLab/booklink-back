package com.booklink.backend.service;

import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.model.Tag;

public interface TagService {
    Tag findOrCreateTag(CreateTagDto createTagDto);
}
