package com.example.project.service;

import com.example.project.dto.BlogRequests.CreateCommentRequest;
import com.example.project.dto.BlogRequests.CreatePostRequest;
import com.example.project.dto.BlogResponses.CommentResponse;
import com.example.project.dto.BlogResponses.PostResponse;
import com.example.project.entity.Comment;
import com.example.project.entity.Post;
import com.example.project.entity.User;
import com.example.project.repository.CommentRepository;
import com.example.project.repository.PostRepository;
import com.example.project.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing blog posts and comments.
 */
@Service
public class BlogService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  /**
   * Creates a new blog post.
   *
   * @param request the request containing post content and author data
   */
  public void createPost(CreatePostRequest request) {
    User author = userRepository.findByUsername(request.getAuthorUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Post post = new Post();
    post.setContent(request.getContent());
    post.setAuthor(author);
    postRepository.save(post);
  }

  /**
   * Retrieves all blog posts sorted by creation date descending.
   *
   * @return a list of post responses
   */
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

  /**
   * Adds a new comment to an existing post.
   *
   * @param request the request containing comment content and target post
   */
  public void addComment(CreateCommentRequest request) {
    User author = userRepository.findByUsername(request.getAuthorUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    Post post = postRepository.findById(request.getPostId())
        .orElseThrow(() -> new IllegalArgumentException("Post not found"));

    Comment comment = new Comment();
    comment.setContent(request.getContent());
    comment.setAuthor(author);
    comment.setPost(post);
    commentRepository.save(comment);
  }

  /**
   * Retrieves all comments for a specific post.
   *
   * @param postId the unique identifier of the post
   * @return a list of comment responses
   */
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