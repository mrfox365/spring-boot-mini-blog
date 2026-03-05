package com.example.project.repository;

import com.example.project.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Post entity.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

  /**
   * Finds all posts, ordered by creation time descending.
   *
   * @return a list of posts
   */
  List<Post> findAllByOrderByCreatedAtDesc();

  /**
   * Finds all posts by a specific author, ordered by creation time descending.
   *
   * @param authorId the ID of the author
   * @return a list of posts
   */
  List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}