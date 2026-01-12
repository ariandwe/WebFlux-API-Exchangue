package com.example.demo.repository;

import com.example.demo.entity.ExchangeRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para operaciones CRUD de tipos de cambio.
 */
@Repository
public interface ExchangeRateRepository extends ReactiveCrudRepository<ExchangeRate, Long> {
    
    /**
     * Busca un tipo de cambio por moneda origen y destino.
     * 
     * @param monedaOrigen Moneda origen
     * @param monedaDestino Moneda destino
     * @return Mono con el ExchangeRate encontrado o vacío
     */
    @Query("SELECT * FROM exchange_rates WHERE moneda_origen = $1 AND moneda_destino = $2")
    Mono<ExchangeRate> findByMonedaOrigenAndMonedaDestino(String monedaOrigen, String monedaDestino);
    
    /**
     * Verifica si existe un tipo de cambio para las monedas dadas.
     * 
     * @param monedaOrigen Moneda origen
     * @param monedaDestino Moneda destino
     * @return Mono<Boolean> true si existe, false en caso contrario
     */
    @Query("SELECT COUNT(*) > 0 FROM exchange_rates WHERE moneda_origen = $1 AND moneda_destino = $2")
    Mono<Boolean> existsByMonedaOrigenAndMonedaDestino(String monedaOrigen, String monedaDestino);
}

