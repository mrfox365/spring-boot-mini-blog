package com.example.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for constructing and sending system emails.
 */
@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  /**
   * Sends a 6-digit verification code to the recipient's email address.
   *
   * @param to   the destination email address
   * @param code the verification code to be included in the message
   */
  public void sendVerificationCode(String to, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Registration Verification Code");
    message.setText("Your code to complete registration: " + code
        + "\nThis code is valid for 15 minutes.");
    mailSender.send(message);
  }

  /**
   * Sends a password reset link containing a unique security token.
   *
   * @param to    the destination email address
   * @param token the security token used for verification in the reset URL
   */
  public void sendPasswordResetLink(String to, String token) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Password Recovery Request");

    String resetUrl = "http://localhost:8080/reset-password.html?token=" + token;
    message.setText("To reset your password, please follow this link: " + resetUrl
        + "\nThis link is valid for 30 minutes.");
    mailSender.send(message);
  }
}