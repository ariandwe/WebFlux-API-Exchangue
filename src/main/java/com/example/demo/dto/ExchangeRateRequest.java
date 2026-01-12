package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear un nuevo tipo de cambio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequest {
    
    @NotBlank(message = "La moneda origen es obligatoria")
    private String monedaOrigen;
    
    @NotBlank(message = "La moneda destino es obligatoria")
    private String monedaDestino;
    
    @NotNull(message = "El tipo de cambio es obligatorio")
    @Positive(message = "El tipo de cambio debe ser positivo")
    private BigDecimal tipoCambio;
}

