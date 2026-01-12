package com.example.demo.service;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio para registrar operaciones de auditoría.
 */
@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    /**
     * Registra una operación de cambio de moneda en el log de auditoría.
     * 
     * @param usuario Usuario que realizó la operación
     * @param monedaOrigen Moneda origen
     * @param monedaDestino Moneda destino
     * @param montoInicial Monto inicial
     * @param montoConvertido Monto convertido
     * @param tipoCambioAplicado Tipo de cambio aplicado
     * @return Mono con el AuditLog guardado
     */
    public Mono<AuditLog> logExchangeOperation(
            String usuario,
            String monedaOrigen,
            String monedaDestino,
            BigDecimal montoInicial,
            BigDecimal montoConvertido,
            BigDecimal tipoCambioAplicado) {
        
        AuditLog auditLog = AuditLog.builder()
                .usuario(usuario)
                .monedaOrigen(monedaOrigen)
                .monedaDestino(monedaDestino)
                .montoInicial(montoInicial)
                .montoConvertido(montoConvertido)
                .tipoCambioAplicado(tipoCambioAplicado)
                .fecha(LocalDateTime.now())
                .build();
        
        return auditLogRepository.save(auditLog);
    }
}

