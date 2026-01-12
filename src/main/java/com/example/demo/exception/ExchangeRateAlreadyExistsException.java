package com.example.demo.exception;

/**
 * Excepción lanzada cuando ya existe un tipo de cambio para las monedas dadas.
 */
public class ExchangeRateAlreadyExistsException extends RuntimeException {
    
    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }
    
    public ExchangeRateAlreadyExistsException(String monedaOrigen, String monedaDestino) {
        super(String.format("Ya existe un tipo de cambio para %s -> %s", monedaOrigen, monedaDestino));
    }
}

