package com.booklink.backend.controller;



import com.booklink.backend.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/tag")
public class TagController {

  private final TagService tagService;
  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

    @GetMapping()
    public ResponseEntity<?> getTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAllTags());
    }



}
