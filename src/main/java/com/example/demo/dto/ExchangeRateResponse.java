package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para un tipo de cambio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {
    
    private Long id;
    private String monedaOrigen;
    private String monedaDestino;
    private BigDecimal tipoCambio;
    private LocalDateTime fechaActualizacion;
}

