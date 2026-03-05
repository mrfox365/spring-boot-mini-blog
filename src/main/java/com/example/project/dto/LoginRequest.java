package com.example.project.dto;

import lombok.Data;

/**
 * DTO for user authentication.
 */
@Data
public class LoginRequest {
  private String login;
  private String password;
}