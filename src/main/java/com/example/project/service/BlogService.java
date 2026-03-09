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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Service class for managing blog posts and user comments.
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
   * Creates a new blog post in the system.
   *
   * @param request the request data containing content and author information
   * @throws IllegalArgumentException if the author is not found
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
   * Retrieves a paginated list of all blog posts, sorted by creation date.
   *
   * @param page the requested page index
   * @param size the number of items per page
   * @return a list of post response DTOs
   */
  public List<PostResponse> getAllPosts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    return postRepository.findAll(pageable).getContent().stream().map(post -> {
      PostResponse dto = new PostResponse();
      dto.setId(post.getId());
      dto.setContent(post.getContent());
      dto.setAuthorUsername(post.getAuthor().getUsername());
      dto.setCreatedAt(post.getCreatedAt());
      return dto;
    }).collect(Collectors.toList());
  }

  /**
   * Adds a new comment to a specified blog post.
   *
   * @param request the request data containing content, author, and post ID
   * @throws IllegalArgumentException if the author or post is not found
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
   * Retrieves all comments associated with a specific post.
   *
   * @param postId the identifier of the post
   * @return a list of comment response DTOs ordered by creation time
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

  /**
   * Deletes a post and all its related comments from the database.
   *
   * @param postId   the identifier of the post to delete
   * @param username the username of the person requesting deletion for verification
   * @throws IllegalArgumentException if the post is not found or user is not the author
   */
  @Transactional
  public void deletePost(Long postId, String username) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("Post not found"));

    if (!post.getAuthor().getUsername().equals(username)) {
      throw new IllegalArgumentException("You can only delete your own posts");
    }

    commentRepository.deleteAllByPostId(postId);
    postRepository.delete(post);
  }

  /**
   * Deletes a specific comment from the database.
   *
   * @param commentId the identifier of the comment to delete
   * @param username  the username of the person requesting deletion for verification
   * @throws IllegalArgumentException if the comment is not found or user is not the author
   */
  @Transactional
  public void deleteComment(Long commentId, String username) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

    if (!comment.getAuthor().getUsername().equals(username)) {
      throw new IllegalArgumentException("You can only delete your own comments");
    }

    commentRepository.delete(comment);
  }
}