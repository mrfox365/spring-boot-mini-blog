package com.example.project.config;

import com.example.project.entity.Post;
import com.example.project.entity.User;
import com.example.project.repository.PostRepository;
import com.example.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("stress")
public class DatabaseStressSeeder {

  @Bean
  public CommandLineRunner seedDatabase(UserRepository userRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      if (postRepository.count() < 10000) {
        System.out.println("Початок генерації 10 000 дописів для стрес-тесту...");

        // Перевіряємо, чи існує юзер Fox. Якщо ні - створюємо, якщо так - просто беремо його з БД.
        User author = userRepository.findByUsername("Fox").orElseGet(() -> {
          User newUser = new User();
          newUser.setUsername("Fox");
          newUser.setPassword(passwordEncoder.encode("09125689"));
          newUser.setEmail("fox_stress@test.com");
          return userRepository.save(newUser);
        });

        // Використовуємо batching (пакетне збереження) для швидкості
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
          Post post = new Post();
          post.setContent("Стрес-тестовий контент номер " + i + ". Перевірка швидкодії бази даних під навантаженням.");
          post.setAuthor(author);
          posts.add(post);

          if (i % 1000 == 0) {
            postRepository.saveAll(posts);
            posts.clear();
            System.out.println("Збережено " + i + " дописів...");
          }
        }
        System.out.println("База даних успішно підготовлена!");
      } else {
        System.out.println("База даних вже має достатньо дописів для тестування.");
      }
    };
  }
}