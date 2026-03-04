package com.example.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Код підтвердження реєстрації");
        message.setText("Ваш код для завершення реєстрації: " + code + "\nКод дійсний 15 хвилин.");
        mailSender.send(message);
    }

    public void sendPasswordResetLink(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Відновлення пароля");
        String resetUrl = "http://localhost:8080/reset-password.html?token=" + token;
        message.setText("Для скидання пароля перейдіть за посиланням: " + resetUrl + "\nПосилання дійсне 30 хвилин.");
        mailSender.send(message);
    }
}