package com.example.project.controller;

import com.example.project.dto.BlogRequests.CreateCommentRequest;
import com.example.project.dto.BlogRequests.CreatePostRequest;
import com.example.project.dto.BlogResponses.CommentResponse;
import com.example.project.dto.BlogResponses.PostResponse;
import com.example.project.service.BlogService;
import java.util.List;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling blog-related operations.
 */
@RestController
@RequestMapping("/api/blog")
public class BlogController {

  @Autowired
  private BlogService blogService;

  /**
   * Creates a new blog post.
   *
   * @param request the request containing post details
   * @return a success response entity
   */
  @PostMapping("/posts")
  public ResponseEntity<String> createPost(@RequestBody CreatePostRequest request) {
    blogService.createPost(request);
    return ResponseEntity.ok("Post created");
  }

  /**
   * Retrieves blog posts with pagination.
   *
   * @param page page number (default 0)
   * @param size number of records per page (default 20)
   * @return a list of post responses
   */
  @GetMapping("/posts")
  public ResponseEntity<List<PostResponse>> getAllPosts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size
  ) {
    return ResponseEntity.ok(blogService.getAllPosts(page, size));
  }

  /**
   * Adds a comment to a specific post.
   *
   * @param request the request containing comment details
   * @return a success response entity
   */
  @PostMapping("/comments")
  public ResponseEntity<String> addComment(@RequestBody CreateCommentRequest request) {
    blogService.addComment(request);
    return ResponseEntity.ok("Comment added");
  }

  /**
   * Retrieves all comments for a given post.
   *
   * @param postId the unique identifier of the post
   * @return a list of comment responses
   */
  @GetMapping("/comments/{postId}")
  public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
    return ResponseEntity.ok(blogService.getCommentsForPost(postId));
  }

  /**
   * Deletes a specific post.
   *
   * @param postId    the ID of the post to delete
   * @param principal the authenticated user requesting deletion
   * @return a success or error response entity
   */
  @DeleteMapping("/posts/{postId}")
  public ResponseEntity<String> deletePost(
      @PathVariable Long postId, Principal principal) {
    blogService.deletePost(postId, principal.getName());
    return ResponseEntity.ok("Post deleted successfully");
  }

  /**
   * Deletes a specific comment.
   *
   * @param commentId the ID of the comment to delete
   * @param principal the authenticated user requesting deletion
   * @return a success or error response entity
   */
  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<String> deleteComment(
      @PathVariable Long commentId, Principal principal) {
    blogService.deleteComment(commentId, principal.getName());
    return ResponseEntity.ok("Comment deleted successfully");
  }
}