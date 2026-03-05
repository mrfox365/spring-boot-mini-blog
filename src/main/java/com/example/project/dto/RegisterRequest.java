package com.example.project.dto;

import lombok.Data;

/**
 * DTO for the first step of user registration.
 */
@Data
public class RegisterRequest {
  private String username;
  private String email;
  private String password;
}