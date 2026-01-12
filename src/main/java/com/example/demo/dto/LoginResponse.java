package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta del login con el token JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String type;
    private String username;
    
    public static LoginResponse of(String token, String username) {
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .username(username)
                .build();
    }
}

