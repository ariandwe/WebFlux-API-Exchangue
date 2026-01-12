package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para aplicar un tipo de cambio a un monto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyExchangeRequest {
    
    @NotBlank(message = "La moneda origen es obligatoria")
    private String monedaOrigen;
    
    @NotBlank(message = "La moneda destino es obligatoria")
    private String monedaDestino;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;
}

