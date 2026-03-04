package com.example.project.repository;

import com.example.project.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmailAndCodeAndType(String email, String code, VerificationCode.CodeType type);
    void deleteByEmailAndType(String email, VerificationCode.CodeType type);
}