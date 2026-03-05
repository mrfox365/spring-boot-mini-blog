package com.example.project.dto;

import lombok.Data;

/**
 * DTO for email verification using a secure code.
 */
@Data
public class VerifyRequest {
  private String username;
  private String email;
  private String password;
  private String code;
}