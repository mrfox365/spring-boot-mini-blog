package com.example.project.dto;
import lombok.Data;
import java.time.LocalDateTime;

public class BlogResponses {

    @Data
    public static class PostResponse {
        private Long id;
        private String content;
        private String authorUsername;
        private LocalDateTime createdAt;
    }

    @Data
    public static class CommentResponse {
        private Long id;
        private String content;
        private String authorUsername;
        private LocalDateTime createdAt;
    }
}