package com.booklink.backend.controller;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
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
        ForumDto forumDto = forumService.createForum(createForumDto, securityUtil.getLoggedUser().getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(forumDto);
    }
}
