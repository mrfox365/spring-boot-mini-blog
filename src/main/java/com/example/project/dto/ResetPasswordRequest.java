package com.example.project.dto;

import lombok.Data;

/**
 * DTO for password reset requests.
 */
@Data
public class ResetPasswordRequest {
  private String token;
  private String newPassword;
}