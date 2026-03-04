package com.example.project.service;

import com.example.project.dto.ProfileDto.*;
import com.example.project.entity.User;
import com.example.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        UserProfileResponse response = new UserProfileResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setBio(user.getBio());
        response.setBirthDate(user.getBirthDate());
        return response;
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        User user = userRepository.findByUsername(request.getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        // Якщо користувач хоче змінити нікнейм
        if (request.getNewUsername() != null && !request.getNewUsername().isEmpty() && !request.getNewUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.getNewUsername()).isPresent()) {
                throw new IllegalArgumentException("Цей нікнейм вже зайнятий");
            }
            user.setUsername(request.getNewUsername());
        }

        // Якщо користувач хоче змінити пароль
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        // Оновлюємо біографію та дату народження
        user.setBio(request.getBio());
        user.setBirthDate(request.getBirthDate());

        userRepository.save(user);
    }
}