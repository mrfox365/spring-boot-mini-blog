package com.example.project.dto;
import lombok.Data;

public class BlogRequests {

    @Data
    public static class CreatePostRequest {
        private String content;
        private String authorUsername;
    }

    @Data
    public static class CreateCommentRequest {
        private Long postId;
        private String content;
        private String authorUsername;
    }
}