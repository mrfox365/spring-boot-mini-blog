package com.example.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for constructing and sending emails.
 */
@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  /**
   * Sends a 6-digit verification code to the specified email address.
   *
   * @param to   the recipient's email address
   * @param code the verification code to send
   */
  public void sendVerificationCode(String to, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Код підтвердження реєстрації");
    message.setText("Ваш код для завершення реєстрації: " + code
        + "\nКод дійсний 15 хвилин.");
    mailSender.send(message);
  }

  /**
   * Sends a password reset link containing a security token.
   *
   * @param to    the recipient's email address
   * @param token the security token for resetting the password
   */
  public void sendPasswordResetLink(String to, String token) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Відновлення пароля");

    String resetUrl = "http://localhost:8080/reset-password.html?token=" + token;
    message.setText("Для скидання пароля перейдіть за посиланням: " + resetUrl
        + "\nПосилання дійсне 30 хвилин.");
    mailSender.send(message);
  }
}