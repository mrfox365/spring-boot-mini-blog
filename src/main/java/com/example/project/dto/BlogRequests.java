package com.example.project.dto;

import lombok.Data;

/**
 * Class containing DTOs for blog requests.
 */
public class BlogRequests {

  /**
   * Request to create a new post.
   */
  @Data
  public static class CreatePostRequest {
    private String content;
    private String authorUsername;
  }

  /**
   * Request to create a comment.
   */
  @Data
  public static class CreateCommentRequest {
    private Long postId;
    private String content;
    private String authorUsername;
  }
}