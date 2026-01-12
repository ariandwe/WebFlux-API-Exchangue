package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Servicio para autenticación de usuarios y generación de tokens JWT.
 */
@Service
public class UserAuthService {
    
    @Autowired
    private ReactiveUserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param loginRequest Credenciales del usuario
     * @return LoginResponse con el token JWT
     */
    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        return userDetailsService.findByUsername(loginRequest.getUsername())
                .filter(userDetails -> passwordEncoder.matches(
                        loginRequest.getPassword(), 
                        userDetails.getPassword()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Credenciales inválidas")))
                .map(userDetails -> {
                    String token = jwtUtil.generateToken(loginRequest.getUsername());
                    return LoginResponse.of(token, loginRequest.getUsername());
                });
    }
}

