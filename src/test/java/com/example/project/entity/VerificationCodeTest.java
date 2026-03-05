package com.example.project.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VerificationCodeTest {

  @Test
  void testIsExpired_WhenDateIsInPast_ShouldReturnTrue() {
    // Arrange (Підготовка)
    VerificationCode code = new VerificationCode();
    // Встановлюємо час, який вже минув (1 година тому)
    code.setExpiryDate(LocalDateTime.now().minusHours(1));

    // Act (Дія)
    boolean isExpired = code.isExpired();

    // Assert (Перевірка)
    assertTrue(isExpired, "Код повинен вважатися простроченим, якщо дата в минулому");
  }

  @Test
  void testIsExpired_WhenDateIsInFuture_ShouldReturnFalse() {
    // Arrange
    VerificationCode code = new VerificationCode();
    // Встановлюємо час у майбутньому (1 година вперед)
    code.setExpiryDate(LocalDateTime.now().plusHours(1));

    // Act
    boolean isExpired = code.isExpired();

    // Assert
    assertFalse(isExpired, "Код НЕ повинен вважатися простроченим, якщо дата в майбутньому");
  }

  @Test
  void testEnumCoverage() {
    // Викликаємо приховані методи, щоб інструмент покриття їх зарахував
    VerificationCode.CodeType[] values = VerificationCode.CodeType.values();
    VerificationCode.CodeType type = VerificationCode.CodeType.valueOf("REGISTRATION");

    // Робимо базові перевірки
    assertEquals(2, values.length);
    assertEquals(VerificationCode.CodeType.REGISTRATION, type);
  }
}