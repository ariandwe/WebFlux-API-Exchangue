package com.example.demo.controllers;

import com.example.demo.entity.AuditLog;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Controlador para consultar datos de la base de datos.
 * Útil para desarrollo y debugging.
 */
@RestController
@RequestMapping("/db")
public class DatabaseController {
    
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    /**
     * Obtiene todos los tipos de cambio.
     * 
     * @return Flux con todos los tipos de cambio
     */
    @GetMapping(value = "/exchange-rates", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }
    
    /**
     * Obtiene todos los logs de auditoría.
     * 
     * @return Flux con todos los logs de auditoría
     */
    @GetMapping(value = "/audit-logs", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}

