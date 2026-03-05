package com.example.project.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VerificationCode entity logic.
 */
class VerificationCodeTest {

  @Test
  void testIsExpired_WhenDateIsInPast_ShouldReturnTrue() {
    VerificationCode code = new VerificationCode();
    code.setExpiryDate(LocalDateTime.now().minusHours(1));

    boolean isExpired = code.isExpired();

    assertTrue(isExpired, "Code should be expired if the date is in the past");
  }

  @Test
  void testIsExpired_WhenDateIsInFuture_ShouldReturnFalse() {
    VerificationCode code = new VerificationCode();
    code.setExpiryDate(LocalDateTime.now().plusHours(1));

    boolean isExpired = code.isExpired();

    assertFalse(isExpired, "Code should not be expired if the date is in the future");
  }

  @Test
  void testEnumCoverage() {
    VerificationCode.CodeType[] values = VerificationCode.CodeType.values();
    VerificationCode.CodeType type = VerificationCode.CodeType.valueOf("REGISTRATION");

    assertEquals(2, values.length);
    assertEquals(VerificationCode.CodeType.REGISTRATION, type);
  }
}