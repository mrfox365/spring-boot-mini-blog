package com.example.project.controller;

import com.example.project.dto.BlogRequests.*;
import com.example.project.dto.BlogResponses.*;
import com.example.project.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @Autowired private BlogService blogService;

    @PostMapping("/posts")
    public ResponseEntity<String> createPost(@RequestBody CreatePostRequest request) {
        blogService.createPost(request);
        return ResponseEntity.ok("Пост створено");
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(blogService.getAllPosts());
    }

    @PostMapping("/comments")
    public ResponseEntity<String> addComment(@RequestBody CreateCommentRequest request) {
        blogService.addComment(request);
        return ResponseEntity.ok("Коментар додано");
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(blogService.getCommentsForPost(postId));
    }
}