package com.example.project.dto;
import lombok.Data;

@Data
public class VerifyRequest {
    private String username;
    private String email;
    private String password;
    private String code;
}