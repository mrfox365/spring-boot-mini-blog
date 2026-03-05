package com.example.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Entity representing a verification code or a password reset token.
 */
@Entity
@Table(name = "verification_codes")
@Data
public class VerificationCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private LocalDateTime expiryDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CodeType type;

  /**
   * Enum defining the purpose of the verification code.
   */
  public enum CodeType {
    REGISTRATION,
    PASSWORD_RESET
  }

  /**
   * Checks if the verification code has expired.
   *
   * @return true if the code is expired, false otherwise
   */
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiryDate);
  }
}