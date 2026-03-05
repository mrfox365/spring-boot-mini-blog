package com.example.project.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Class containing DTOs for blog responses.
 */
public class BlogResponses {

  /**
   * Response representing a post.
   */
  @Data
  public static class PostResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
  }

  /**
   * Response representing a comment.
   */
  @Data
  public static class CommentResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
  }
}