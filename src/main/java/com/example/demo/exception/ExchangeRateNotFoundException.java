package com.example.demo.exception;

/**
 * Excepción lanzada cuando no se encuentra un tipo de cambio.
 */
public class ExchangeRateNotFoundException extends RuntimeException {
    
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
    
    public ExchangeRateNotFoundException(String monedaOrigen, String monedaDestino) {
        super(String.format("No se encontró tipo de cambio para %s -> %s", monedaOrigen, monedaDestino));
    }
}

