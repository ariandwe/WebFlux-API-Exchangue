package com.example.demo.service;

import com.example.demo.dto.ApplyExchangeRequest;
import com.example.demo.dto.ApplyExchangeResponse;
import com.example.demo.entity.AuditLog;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.exception.ExchangeRateNotFoundException;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para ExchangeApplyService.
 */
@ExtendWith(MockitoExtension.class)
class ExchangeApplyServiceTest {
    
    @Mock
    private ExchangeRateRepository exchangeRateRepository;
    
    @Mock
    private AuditService auditService;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private ExchangeApplyService exchangeApplyService;
    
    private ExchangeRate exchangeRate;
    private ApplyExchangeRequest request;
    
    @BeforeEach
    void setUp() {
        exchangeRate = ExchangeRate.builder()
                .id(1L)
                .monedaOrigen("PEN")
                .monedaDestino("USD")
                .tipoCambio(new BigDecimal("0.27"))
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        request = new ApplyExchangeRequest();
        request.setMonedaOrigen("PEN");
        request.setMonedaDestino("USD");
        request.setMonto(new BigDecimal("100.00"));
        
        when(authentication.getName()).thenReturn("admin");
    }
    
    @Test
    void testApplyExchange_Success() {
        when(exchangeRateRepository.findByMonedaOrigenAndMonedaDestino("PEN", "USD"))
                .thenReturn(Mono.just(exchangeRate));
        when(auditService.logExchangeOperation(
                anyString(), anyString(), anyString(), 
                any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Mono.just(AuditLog.builder().build()));
        
        StepVerifier.create(
                exchangeApplyService.applyExchange(request)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        )
                .expectNextMatches(response -> 
                    response.getMonedaOrigen().equals("PEN") &&
                    response.getMonedaDestino().equals("USD") &&
                    response.getMontoInicial().equals(new BigDecimal("100.00")) &&
                    response.getMontoConvertido().equals(new BigDecimal("27.00")) &&
                    response.getTipoCambioAplicado().equals(new BigDecimal("0.27"))
                )
                .verifyComplete();
    }
    
    @Test
    void testApplyExchange_ExchangeRateNotFound() {
        when(exchangeRateRepository.findByMonedaOrigenAndMonedaDestino("PEN", "USD"))
                .thenReturn(Mono.empty());
        
        StepVerifier.create(
                exchangeApplyService.applyExchange(request)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        )
                .expectError(ExchangeRateNotFoundException.class)
                .verify();
    }
}

