package com.example.demo.controllers;

import com.example.demo.dto.ExchangeRateRequest;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.dto.ExchangeRateUpdateRequest;
import com.example.demo.service.ExchangeRateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador para operaciones CRUD de tipos de cambio.
 */
@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    /**
     * Crea un nuevo tipo de cambio.
     * 
     * @param request Datos del tipo de cambio
     * @return ExchangeRateResponse con el tipo de cambio creado
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ExchangeRateResponse> create(@Valid @RequestBody Mono<ExchangeRateRequest> request) {
        return request.flatMap(exchangeRateService::create);
    }
    
    /**
     * Actualiza un tipo de cambio existente.
     * 
     * @param id ID del tipo de cambio
     * @param request Datos actualizados
     * @return ExchangeRateResponse con el tipo de cambio actualizado
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ExchangeRateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody Mono<ExchangeRateUpdateRequest> request) {
        return request.flatMap(req -> exchangeRateService.update(id, req));
    }
    
    /**
     * Busca un tipo de cambio por moneda origen y destino.
     * 
     * @param origen Moneda origen
     * @param destino Moneda destino
     * @return ExchangeRateResponse con el tipo de cambio encontrado
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ExchangeRateResponse> findByMonedas(
            @RequestParam String origen,
            @RequestParam String destino) {
        return exchangeRateService.findByMonedas(origen, destino);
    }
    
    /**
     * Obtiene todos los tipos de cambio.
     * 
     * @return Flux con todos los tipos de cambio
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<ExchangeRateResponse> findAll() {
        return exchangeRateService.findAll();
    }
    
    /**
     * Obtiene un tipo de cambio por ID.
     * 
     * @param id ID del tipo de cambio
     * @return ExchangeRateResponse con el tipo de cambio encontrado
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ExchangeRateResponse> findById(@PathVariable Long id) {
        return exchangeRateService.findById(id);
    }
    
    /**
     * Elimina un tipo de cambio por ID.
     * 
     * @param id ID del tipo de cambio
     * @return Mono vacío
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return exchangeRateService.delete(id);
    }
}

