package com.example.project.repository;

import com.example.project.entity.VerificationCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for VerificationCode entity.
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

  /**
   * Finds a verification code by email, code value, and code type.
   *
   * @param email the user's email
   * @param code  the verification code or token
   * @param type  the purpose of the code
   * @return an Optional containing the verification code if found
   */
  Optional<VerificationCode> findByEmailAndCodeAndType(
      String email, String code, VerificationCode.CodeType type);

  /**
   * Deletes a verification code by email and code type.
   *
   * @param email the user's email
   * @param type  the purpose of the code
   */
  void deleteByEmailAndType(String email, VerificationCode.CodeType type);
}