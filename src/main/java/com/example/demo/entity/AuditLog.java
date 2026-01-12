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
 * Entidad que registra la auditoría de todas las operaciones de cambio de moneda.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("audit_logs")
public class AuditLog {
    
    @Id
    private Long id;
    
    @Column("usuario")
    private String usuario;
    
    @Column("moneda_origen")
    private String monedaOrigen;
    
    @Column("moneda_destino")
    private String monedaDestino;
    
    @Column("monto_inicial")
    private BigDecimal montoInicial;
    
    @Column("monto_convertido")
    private BigDecimal montoConvertido;
    
    @Column("tipo_cambio_aplicado")
    private BigDecimal tipoCambioAplicado;
    
    @Column("fecha")
    private LocalDateTime fecha;
}

