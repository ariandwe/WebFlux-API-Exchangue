package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un tipo de cambio entre dos monedas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("exchange_rates")
public class ExchangeRate {
    
    @Id
    private Long id;
    
    @Column("moneda_origen")
    private String monedaOrigen;
    
    @Column("moneda_destino")
    private String monedaDestino;
    
    @Column("tipo_cambio")
    private BigDecimal tipoCambio;
    
    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}

