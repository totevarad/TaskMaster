package com.taskmaster.controllers;

import com.taskmaster.models.Tag;
import com.taskmaster.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return new ResponseEntity<>(tagRepository.save(tag), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepository.findAll());
    }
}
