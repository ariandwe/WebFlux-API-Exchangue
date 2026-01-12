package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta al aplicar un tipo de cambio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyExchangeResponse {
    
    private String monedaOrigen;
    private String monedaDestino;
    private BigDecimal montoInicial;
    private BigDecimal montoConvertido;
    private BigDecimal tipoCambioAplicado;
    private LocalDateTime fecha;
}

