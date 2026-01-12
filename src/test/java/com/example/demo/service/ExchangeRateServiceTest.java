package com.example.demo.service;

import com.example.demo.dto.ExchangeRateRequest;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.dto.ExchangeRateUpdateRequest;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.exception.ExchangeRateAlreadyExistsException;
import com.example.demo.exception.ExchangeRateNotFoundException;
import com.example.demo.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para ExchangeRateService.
 */
@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {
    
    @Mock
    private ExchangeRateRepository exchangeRateRepository;
    
    @InjectMocks
    private ExchangeRateService exchangeRateService;
    
    private ExchangeRate exchangeRate;
    private ExchangeRateRequest request;
    
    @BeforeEach
    void setUp() {
        exchangeRate = ExchangeRate.builder()
                .id(1L)
                .monedaOrigen("PEN")
                .monedaDestino("USD")
                .tipoCambio(new BigDecimal("0.27"))
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        request = new ExchangeRateRequest();
        request.setMonedaOrigen("PEN");
        request.setMonedaDestino("USD");
        request.setTipoCambio(new BigDecimal("0.27"));
    }
    
    @Test
    void testCreate_Success() {
        when(exchangeRateRepository.existsByMonedaOrigenAndMonedaDestino(anyString(), anyString()))
                .thenReturn(Mono.just(false));
        when(exchangeRateRepository.save(any(ExchangeRate.class)))
                .thenReturn(Mono.just(exchangeRate));
        
        StepVerifier.create(exchangeRateService.create(request))
                .expectNextMatches(response -> 
                    response.getMonedaOrigen().equals("PEN") &&
                    response.getMonedaDestino().equals("USD") &&
                    response.getTipoCambio().equals(new BigDecimal("0.27"))
                )
                .verifyComplete();
    }
    
    @Test
    void testCreate_AlreadyExists() {
        when(exchangeRateRepository.existsByMonedaOrigenAndMonedaDestino(anyString(), anyString()))
                .thenReturn(Mono.just(true));
        
        StepVerifier.create(exchangeRateService.create(request))
                .expectError(ExchangeRateAlreadyExistsException.class)
                .verify();
    }
    
    @Test
    void testUpdate_Success() {
        ExchangeRateUpdateRequest updateRequest = new ExchangeRateUpdateRequest();
        updateRequest.setTipoCambio(new BigDecimal("0.28"));
        
        ExchangeRate updated = ExchangeRate.builder()
                .id(1L)
                .monedaOrigen("PEN")
                .monedaDestino("USD")
                .tipoCambio(new BigDecimal("0.28"))
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        when(exchangeRateRepository.findById(1L))
                .thenReturn(Mono.just(exchangeRate));
        when(exchangeRateRepository.save(any(ExchangeRate.class)))
                .thenReturn(Mono.just(updated));
        
        StepVerifier.create(exchangeRateService.update(1L, updateRequest))
                .expectNextMatches(response -> 
                    response.getTipoCambio().equals(new BigDecimal("0.28"))
                )
                .verifyComplete();
    }
    
    @Test
    void testUpdate_NotFound() {
        ExchangeRateUpdateRequest updateRequest = new ExchangeRateUpdateRequest();
        updateRequest.setTipoCambio(new BigDecimal("0.28"));
        
        when(exchangeRateRepository.findById(1L))
                .thenReturn(Mono.empty());
        
        StepVerifier.create(exchangeRateService.update(1L, updateRequest))
                .expectError(ExchangeRateNotFoundException.class)
                .verify();
    }
    
    @Test
    void testFindByMonedas_Success() {
        when(exchangeRateRepository.findByMonedaOrigenAndMonedaDestino("PEN", "USD"))
                .thenReturn(Mono.just(exchangeRate));
        
        StepVerifier.create(exchangeRateService.findByMonedas("PEN", "USD"))
                .expectNextMatches(response -> 
                    response.getMonedaOrigen().equals("PEN") &&
                    response.getMonedaDestino().equals("USD")
                )
                .verifyComplete();
    }
    
    @Test
    void testFindByMonedas_NotFound() {
        when(exchangeRateRepository.findByMonedaOrigenAndMonedaDestino("PEN", "USD"))
                .thenReturn(Mono.empty());
        
        StepVerifier.create(exchangeRateService.findByMonedas("PEN", "USD"))
                .expectError(ExchangeRateNotFoundException.class)
                .verify();
    }
}

