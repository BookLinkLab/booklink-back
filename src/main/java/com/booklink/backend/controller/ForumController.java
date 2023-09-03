package com.booklink.backend.controller;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.EditForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.user.UpdateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.service.ForumService;
import com.booklink.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forum")
public class ForumController {
    private final ForumService forumService;
    private final SecurityUtil securityUtil;

    public ForumController(ForumService forumService, SecurityUtil securityUtil) {
        this.forumService = forumService;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    public ResponseEntity<ForumDto> createForum(@Valid @RequestBody CreateForumDto createForumDto) {
        ForumDto forumDto = forumService.createForum(createForumDto, Long.valueOf(securityUtil.getLoggedUser().getUsername()));
        return ResponseEntity.status(HttpStatus.CREATED).body(forumDto);
    }

    @PatchMapping("/{id}")
    public ForumDto editForum(@PathVariable Long id, @Valid @RequestBody EditForumDto editForumDto) {
        return this.forumService.editForum(id, editForumDto);
    }

    @PostMapping("/{id}/tag")
    public ForumDto addTagToForum(@PathVariable Long id, @Valid @RequestBody CreateTagDto createTagDto){
        return forumService.addTagToForum(id, Long.valueOf(securityUtil.getLoggedUser().getUsername()), createTagDto);
    }
}
