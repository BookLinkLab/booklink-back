package com.booklink.backend.service.impl;

import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.model.Tag;
import com.booklink.backend.repository.TagRepository;
import com.booklink.backend.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag findOrCreateTag(CreateTagDto createTagDto) {
        Optional<Tag> optionalTag = tagRepository.findByName(createTagDto.getName());
        if (optionalTag.isPresent()) {
            return optionalTag.get();
        }
        Tag tag = Tag.from(createTagDto);
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
