package com.example.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to bootstrap the Spring Boot application.
 */
@SpringBootApplication
public class ProjectApplication {

  /**
   * Main application entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ProjectApplication.class, args);
  }
}