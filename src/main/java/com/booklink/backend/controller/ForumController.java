package com.booklink.backend.controller;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.EditForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.forum.ForumGetDto;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.service.ForumService;
import com.booklink.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        ForumDto forumDto = forumService.createForum(createForumDto, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(forumDto);
    }

    @PostMapping("/{id}/join")
    public ForumDto joinForum(@PathVariable Long id) {
        return forumService.joinForum(id, securityUtil.getLoggedUserId());
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<?> leaveForum(@PathVariable Long id) {
        forumService.leaveForum(id, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Foro %s abandonado correctamente".formatted(id));
    }

    @PatchMapping("/{id}")
    public ForumDto editForum(@PathVariable Long id, @Valid @RequestBody EditForumDto editForumDto) {
        return this.forumService.editForum(id, securityUtil.getLoggedUserId(), editForumDto);
    }

    @PostMapping("/{id}/tag")
    public ForumDto addTagToForum(@PathVariable Long id, @Valid @RequestBody CreateTagDto createTagDto) {
        return forumService.addTagToForum(id, securityUtil.getLoggedUserId(), createTagDto);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchForums(@RequestParam(name = "searchTerm", required = false) String searchTerm) {
        return ResponseEntity.status(HttpStatus.OK).body(forumService.searchForums(searchTerm, securityUtil.getLoggedUserId()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteForum(@PathVariable Long id) {
        forumService.deleteForum(id, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Foro %s eliminado".formatted(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumGetDto> getForumById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(forumService.getForumById(id,securityUtil.getLoggedUserId()));
    }

}
