package com.example.project.repository;

import com.example.project.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for Comment entity.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

  /**
   * Finds all comments for a specific post, ordered by creation time ascending.
   *
   * @param postId the ID of the post
   * @return a list of comments
   */
  List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

  /**
   * Deletes all comments associated with a specific post.
   *
   * @param postId the ID of the post
   */
  @Transactional
  void deleteAllByPostId(Long postId);
}