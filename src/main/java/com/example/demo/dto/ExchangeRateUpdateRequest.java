package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para actualizar un tipo de cambio existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateUpdateRequest {
    
    @NotNull(message = "El tipo de cambio es obligatorio")
    @Positive(message = "El tipo de cambio debe ser positivo")
    private BigDecimal tipoCambio;
}

