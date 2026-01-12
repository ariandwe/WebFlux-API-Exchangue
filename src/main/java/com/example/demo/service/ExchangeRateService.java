2package com.example.demo.service;

import com.example.demo.dto.ExchangeRateRequest;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.dto.ExchangeRateUpdateRequest;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.exception.ExchangeRateAlreadyExistsException;
import com.example.demo.exception.ExchangeRateNotFoundException;
import com.example.demo.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Servicio para operaciones CRUD de tipos de cambio.
 */
@Service
public class ExchangeRateService {
    
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    
    /**
     * Crea un nuevo tipo de cambio.
     * 
     * @param request Datos del tipo de cambio
     * @return Mono con el ExchangeRateResponse creado
     */
    public Mono<ExchangeRateResponse> create(ExchangeRateRequest request) {
        return exchangeRateRepository
                .existsByMonedaOrigenAndMonedaDestino(
                        request.getMonedaOrigen(), 
                        request.getMonedaDestino())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ExchangeRateAlreadyExistsException(
                                request.getMonedaOrigen(), 
                                request.getMonedaDestino()));
                    }
                    
                    ExchangeRate exchangeRate = ExchangeRate.builder()
                            .monedaOrigen(request.getMonedaOrigen())
                            .monedaDestino(request.getMonedaDestino())
                            .tipoCambio(request.getTipoCambio())
                            .fechaActualizacion(LocalDateTime.now())
                            .build();
                    
                    return exchangeRateRepository.save(exchangeRate)
                            .map(this::toResponse);
                });
    }
    
    /**
     * Actualiza un tipo de cambio existente.
     * 
     * @param id ID del tipo de cambio
     * @param request Datos actualizados
     * @return Mono con el ExchangeRateResponse actualizado
     */
    public Mono<ExchangeRateResponse> update(Long id, ExchangeRateUpdateRequest request) {
        return exchangeRateRepository.findById(id)
                .switchIfEmpty(Mono.error(new ExchangeRateNotFoundException(
                        "No se encontró tipo de cambio con ID: " + id)))
                .flatMap(exchangeRate -> {
                    exchangeRate.setTipoCambio(request.getTipoCambio());
                    exchangeRate.setFechaActualizacion(LocalDateTime.now());
                    return exchangeRateRepository.save(exchangeRate)
                            .map(this::toResponse);
                });
    }
    
    /**
     * Busca un tipo de cambio por moneda origen y destino.
     * 
     * @param monedaOrigen Moneda origen
     * @param monedaDestino Moneda destino
     * @return Mono con el ExchangeRateResponse encontrado
     */
    public Mono<ExchangeRateResponse> findByMonedas(String monedaOrigen, String monedaDestino) {
        return exchangeRateRepository
                .findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
                .switchIfEmpty(Mono.error(new ExchangeRateNotFoundException(
                        monedaOrigen, monedaDestino)))
                .map(this::toResponse);
    }
    
    /**
     * Obtiene todos los tipos de cambio.
     * 
     * @return Flux con todos los ExchangeRateResponse
     */
    public Flux<ExchangeRateResponse> findAll() {
        return exchangeRateRepository.findAll()
                .map(this::toResponse);
    }
    
    /**
     * Obtiene un tipo de cambio por ID.
     * 
     * @param id ID del tipo de cambio
     * @return Mono con el ExchangeRateResponse encontrado
     */
    public Mono<ExchangeRateResponse> findById(Long id) {
        return exchangeRateRepository.findById(id)
                .switchIfEmpty(Mono.error(new ExchangeRateNotFoundException(
                        "No se encontró tipo de cambio con ID: " + id)))
                .map(this::toResponse);
    }
    
    /**
     * Elimina un tipo de cambio por ID.
     * 
     * @param id ID del tipo de cambio
     * @return Mono vacío
     */
    public Mono<Void> delete(Long id) {
        return exchangeRateRepository.findById(id)
                .switchIfEmpty(Mono.error(new ExchangeRateNotFoundException(
                        "No se encontró tipo de cambio con ID: " + id)))
                .flatMap(exchangeRateRepository::delete);
    }
    
    /**
     * Convierte una entidad ExchangeRate a DTO ExchangeRateResponse.
     */
    private ExchangeRateResponse toResponse(ExchangeRate exchangeRate) {
        return ExchangeRateResponse.builder()
                .id(exchangeRate.getId())
                .monedaOrigen(exchangeRate.getMonedaOrigen())
                .monedaDestino(exchangeRate.getMonedaDestino())
                .tipoCambio(exchangeRate.getTipoCambio())
                .fechaActualizacion(exchangeRate.getFechaActualizacion())
                .build();
    }
}

