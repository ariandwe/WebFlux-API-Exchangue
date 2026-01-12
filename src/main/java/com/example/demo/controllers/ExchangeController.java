package com.example.demo.controllers;

import com.example.demo.dto.ApplyExchangeRequest;
import com.example.demo.dto.ApplyExchangeResponse;
import com.example.demo.service.ExchangeApplyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador para aplicar tipos de cambio a montos.
 */
@RestController
@RequestMapping("/exchange")
public class ExchangeController {
    
    @Autowired
    private ExchangeApplyService exchangeApplyService;
    
    /**
     * Aplica un tipo de cambio a un monto y registra la operación en auditoría.
     * 
     * @param request Datos de la operación (monedas y monto)
     * @return ApplyExchangeResponse con el resultado de la conversión
     */
    @PostMapping(value = "/apply", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApplyExchangeResponse> applyExchange(@Valid @RequestBody Mono<ApplyExchangeRequest> request) {
        return request.flatMap(exchangeApplyService::applyExchange);
    }
}

