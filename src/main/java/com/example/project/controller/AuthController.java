package com.example.project.controller;

import com.example.project.dto.*;
import com.example.project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody RegisterRequest request) {
        try {
            authService.sendRegistrationCode(request.getEmail());
            return ResponseEntity.ok("Код успішно відправлено на пошту");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка відправки листа");
        }
    }

    @PostMapping("/verify-and-save")
    public ResponseEntity<String> verifyAndSave(@RequestBody VerifyRequest request) {
        try {
            authService.completeRegistration(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getCode()
            );
            return ResponseEntity.ok("Реєстрація успішно завершена");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        boolean isAuthenticated = authService.authenticate(request.getLogin(), request.getPassword());

        if (isAuthenticated) {
            // У повноцінному додатку тут зазвичай генерують і повертають JWT токен
            return ResponseEntity.ok("Вхід успішний!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невірний логін або пароль");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {
        // Ми завжди повертаємо OK, щоб не розкривати, чи існує такий email у базі (захист від Enumeration)
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Якщо вказаний email існує, на нього відправлено посилання для відновлення");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Пароль успішно змінено");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}