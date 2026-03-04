package com.example.project.service;

import com.example.project.dto.BlogRequests.*;
import com.example.project.dto.BlogResponses.*;
import com.example.project.entity.Comment;
import com.example.project.entity.Post;
import com.example.project.entity.User;
import com.example.project.repository.CommentRepository;
import com.example.project.repository.PostRepository;
import com.example.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private UserRepository userRepository;

    public void createPost(CreatePostRequest request) {
        User author = userRepository.findByUsername(request.getAuthorUsername())
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        Post post = new Post();
        post.setContent(request.getContent());
        post.setAuthor(author);
        postRepository.save(post);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(post -> {
            PostResponse dto = new PostResponse();
            dto.setId(post.getId());
            dto.setContent(post.getContent());
            dto.setAuthorUsername(post.getAuthor().getUsername());
            dto.setCreatedAt(post.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public void addComment(CreateCommentRequest request) {
        User author = userRepository.findByUsername(request.getAuthorUsername())
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Пост не знайдено"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setPost(post);
        commentRepository.save(comment);
    }

    public List<CommentResponse> getCommentsForPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream().map(comment -> {
            CommentResponse dto = new CommentResponse();
            dto.setId(comment.getId());
            dto.setContent(comment.getContent());
            dto.setAuthorUsername(comment.getAuthor().getUsername());
            dto.setCreatedAt(comment.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }
}