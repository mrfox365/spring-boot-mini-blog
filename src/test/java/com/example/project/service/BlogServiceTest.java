package com.example.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for BlogService.
 */
@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private CommentRepository commentRepository;

  @InjectMocks
  private BlogService blogService;

  private User testUser;
  private Post testPost;
  private Comment testComment;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testAuthor");

    testPost = new Post();
    testPost.setId(100L);
    testPost.setContent("Test post content");
    testPost.setAuthor(testUser);
    testPost.setCreatedAt(LocalDateTime.now());

    testComment = new Comment();
    testComment.setId(50L);
    testComment.setContent("Test comment");
    testComment.setAuthor(testUser);
    testComment.setPost(testPost);
    testComment.setCreatedAt(LocalDateTime.now());
  }

  @Test
  void createPost_SuccessfulCreation() {
    CreatePostRequest request = new CreatePostRequest();
    request.setAuthorUsername("testAuthor");
    request.setContent("Hello World!");

    when(userRepository.findByUsername("testAuthor")).thenReturn(Optional.of(testUser));

    blogService.createPost(request);

    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void createPost_UserNotFound_ThrowsException() {
    CreatePostRequest request = new CreatePostRequest();
    request.setAuthorUsername("unknown");

    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      blogService.createPost(request);
    });

    // Оновлено текст на англійський
    assertEquals("User not found", exception.getMessage());
    verify(postRepository, never()).save(any(Post.class));
  }

  @Test
  void getAllPosts_ReturnsMappedDtoList() {
    // 1. Створюємо "фейкову" сторінку (Page), яка містить наш тестовий пост
    Page<Post> postPage = new PageImpl<>(List.of(testPost));

    // 2. Вказуємо Mockito повертати цю сторінку, коли викликається findAll(Pageable)
    when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

    // 3. Викликаємо оновлений метод сервісу (передаємо сторінку 0, розмір 20)
    List<PostResponse> responses = blogService.getAllPosts(0, 20);

    // 4. Перевірки (assertions) залишаються без змін!
    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals(100L, responses.get(0).getId());
    assertEquals("Test post content", responses.get(0).getContent());
    assertEquals("testAuthor", responses.get(0).getAuthorUsername());
  }

  @Test
  void addComment_SuccessfulAddition() {
    CreateCommentRequest request = new CreateCommentRequest();
    request.setAuthorUsername("testAuthor");
    request.setPostId(100L);
    request.setContent("My comment");

    when(userRepository.findByUsername("testAuthor")).thenReturn(Optional.of(testUser));
    when(postRepository.findById(100L)).thenReturn(Optional.of(testPost));

    blogService.addComment(request);

    verify(commentRepository, times(1)).save(any(Comment.class));
  }

  @Test
  void addComment_PostNotFound_ThrowsException() {
    CreateCommentRequest request = new CreateCommentRequest();
    request.setAuthorUsername("testAuthor");
    request.setPostId(999L);

    when(userRepository.findByUsername("testAuthor")).thenReturn(Optional.of(testUser));
    when(postRepository.findById(999L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      blogService.addComment(request);
    });

    // Оновлено текст на англійський
    assertEquals("Post not found", exception.getMessage());
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void getCommentsForPost_ReturnsMappedDtoList() {
    when(commentRepository.findByPostIdOrderByCreatedAtAsc(100L)).thenReturn(List.of(testComment));

    List<CommentResponse> responses = blogService.getCommentsForPost(100L);

    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals(50L, responses.get(0).getId());
    assertEquals("Test comment", responses.get(0).getContent());
    assertEquals("testAuthor", responses.get(0).getAuthorUsername());
  }
}