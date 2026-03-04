package com.example.project.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}