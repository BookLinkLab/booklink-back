package com.booklink.backend.service;


import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.dto.tag.TagDto;
import com.booklink.backend.model.Tag;
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
public class TagServiceTest {
    @Autowired
    private TagService tagService;

    @Test
    void happyPathTest() {
        assertTrue(tagService.getAllTags().isEmpty());

        CreateTagDto createTagDto = CreateTagDto.builder()
                .name("Tag")
                .build();

        TagDto savedTag = TagDto.from(tagService.findOrCreateTag(createTagDto));
        List<Tag> tags = tagService.getAllTags();
        assertFalse(tags.isEmpty());
        assertEquals(1, tags.size());

        TagDto myTag = TagDto.from(tags.get(0));
        assertEquals(savedTag, myTag);

        TagDto sameTag = TagDto.from(tagService.findOrCreateTag(createTagDto));
        tags = tagService.getAllTags();
        assertEquals(1, tags.size());
        assertEquals(savedTag, sameTag);
    }
}