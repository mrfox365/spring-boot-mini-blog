package com.example.project.config;

import com.example.project.entity.User;
import com.example.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This class runs ONLY on GitHub Actions (due to the "ci" profile).
 * It automatically creates a test user in an empty database
 * so that automated tests can successfully authenticate.
 */
@Configuration
@Profile("ci")
public class TestUserSeeder {

  @Bean
  public CommandLineRunner initTestUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      // Check if the user already exists
      if (userRepository.findByUsername("Fox").isEmpty()) {
        User testUser = new User();
        testUser.setUsername("Fox");

        testUser.setPassword(passwordEncoder.encode("09125689"));

        testUser.setEmail("fox@test.com");

        userRepository.save(testUser);
        System.out.println("✅ Test user 'Fox' has been successfully created with ENCODED password!");
      }
    };
  }
}