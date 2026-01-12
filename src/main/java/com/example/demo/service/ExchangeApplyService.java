package com.example.demo.service;

import com.example.demo.dto.ApplyExchangeRequest;
import com.example.demo.dto.ApplyExchangeResponse;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Servicio para aplicar tipos de cambio a montos y registrar auditoría.
 */
@Service
public class ExchangeApplyService {
    
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    
    @Autowired
    private AuditService auditService;
    
    /**
     * Aplica un tipo de cambio a un monto y registra la operación en auditoría.
     * 
     * @param request Datos de la operación (monedas y monto)
     * @return Mono con el ApplyExchangeResponse
     */
    public Mono<ApplyExchangeResponse> applyExchange(ApplyExchangeRequest request) {
        return exchangeRateRepository
                .findByMonedaOrigenAndMonedaDestino(
                        request.getMonedaOrigen(), 
                        request.getMonedaDestino())
                .switchIfEmpty(Mono.error(new com.example.demo.exception.ExchangeRateNotFoundException(
                        request.getMonedaOrigen(), 
                        request.getMonedaDestino())))
                .flatMap(exchangeRate -> {
                    BigDecimal montoConvertido = calculateConvertedAmount(
                            request.getMonto(), 
                            exchangeRate.getTipoCambio());
                    
                    ApplyExchangeResponse response = ApplyExchangeResponse.builder()
                            .monedaOrigen(request.getMonedaOrigen())
                            .monedaDestino(request.getMonedaDestino())
                            .montoInicial(request.getMonto())
                            .montoConvertido(montoConvertido)
                            .tipoCambioAplicado(exchangeRate.getTipoCambio())
                            .fecha(LocalDateTime.now())
                            .build();
                    
                    // Obtener el usuario autenticado y registrar auditoría
                    return getCurrentUsername()
                            .flatMap(username -> 
                                auditService.logExchangeOperation(
                                        username,
                                        request.getMonedaOrigen(),
                                        request.getMonedaDestino(),
                                        request.getMonto(),
                                        montoConvertido,
                                        exchangeRate.getTipoCambio()
                                )
                            )
                            .thenReturn(response);
                });
    }
    
    /**
     * Calcula el monto convertido aplicando el tipo de cambio.
     * 
     * @param monto Monto inicial
     * @param tipoCambio Tipo de cambio
     * @return Monto convertido
     */
    private BigDecimal calculateConvertedAmount(BigDecimal monto, BigDecimal tipoCambio) {
        return monto.multiply(tipoCambio)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Obtiene el nombre de usuario del contexto de seguridad actual.
     * 
     * @return Mono con el nombre de usuario
     */
    private Mono<String> getCurrentUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .defaultIfEmpty("anonymous");
    }
}

