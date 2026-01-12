package com.example.demo.controllers;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.service.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador para autenticación y generación de tokens JWT.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserAuthService userAuthService;
    
    /**
     * Endpoint para autenticación y obtención de token JWT.
     * 
     * @param loginRequest Credenciales del usuario
     * @return LoginResponse con el token JWT
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<LoginResponse> login(@Valid @RequestBody Mono<LoginRequest> loginRequest) {
        return loginRequest
                .flatMap(userAuthService::login);
    }
}

