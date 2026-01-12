# 🎓 Ejemplo Práctico: Construir un Endpoint desde Cero

## 🎯 Objetivo
Construir el endpoint `GET /exchange-rate?origen=PEN&destino=USD` paso a paso.

---

## Paso 1: Crear la Entidad (5 min)

**Archivo:** `src/main/java/com/example/demo/entity/ExchangeRate.java`

```java
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("exchange_rates")
public class ExchangeRate {
    
    @Id
    private Long id;
    
    @Column("moneda_origen")
    private String monedaOrigen;
    
    @Column("moneda_destino")
    private String monedaDestino;
    
    @Column("tipo_cambio")
    private BigDecimal tipoCambio;
    
    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
```

**✅ Prueba:** Compila el proyecto. Si compila, sigue.

---

## Paso 2: Crear el Schema SQL (3 min)

**Archivo:** `src/main/resources/schema.sql`

```sql
CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    moneda_origen VARCHAR(10) NOT NULL,
    moneda_destino VARCHAR(10) NOT NULL,
    tipo_cambio DECIMAL(20, 6) NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    UNIQUE(moneda_origen, moneda_destino)
);
```

**✅ Prueba:** Ejecuta la app. Verifica en logs que no hay errores de BD.

---

## Paso 3: Crear el Repositorio (5 min)

**Archivo:** `src/main/java/com/example/demo/repository/ExchangeRateRepository.java`

```java
package com.example.demo.repository;

import com.example.demo.entity.ExchangeRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ExchangeRateRepository 
    extends ReactiveCrudRepository<ExchangeRate, Long> {
    
    @Query("SELECT * FROM exchange_rates WHERE moneda_origen = $1 AND moneda_destino = $2")
    Mono<ExchangeRate> findByMonedaOrigenAndMonedaDestino(
        String monedaOrigen, 
        String monedaDestino
    );
}
```

**✅ Prueba:** Compila. Si compila, el repositorio está bien.

---

## Paso 4: Crear el Servicio (10 min)

**Archivo:** `src/main/java/com/example/demo/service/ExchangeRateService.java`

```java
package com.example.demo.service;

import com.example.demo.entity.ExchangeRate;
import com.example.demo.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ExchangeRateService {
    
    @Autowired
    private ExchangeRateRepository repository;
    
    public Mono<ExchangeRate> findByMonedas(String origen, String destino) {
        return repository.findByMonedaOrigenAndMonedaDestino(origen, destino);
    }
}
```

**✅ Prueba:** Compila. Si compila, el servicio está bien.

---

## Paso 5: Crear el DTO de Response (3 min)

**Archivo:** `src/main/java/com/example/demo/dto/ExchangeRateResponse.java`

```java
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {
    
    private Long id;
    private String monedaOrigen;
    private String monedaDestino;
    private BigDecimal tipoCambio;
    private LocalDateTime fechaActualizacion;
}
```

**✅ Prueba:** Compila.

---

## Paso 6: Crear el Controlador (10 min)

**Archivo:** `src/main/java/com/example/demo/controllers/ExchangeRateController.java`

```java
package com.example.demo.controllers;

import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {
    
    @Autowired
    private ExchangeRateService service;
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ExchangeRateResponse> findByMonedas(
            @RequestParam String origen,
            @RequestParam String destino) {
        
        return service.findByMonedas(origen, destino)
                .map(this::toResponse);
    }
    
    private ExchangeRateResponse toResponse(ExchangeRate entity) {
        return ExchangeRateResponse.builder()
                .id(entity.getId())
                .monedaOrigen(entity.getMonedaOrigen())
                .monedaDestino(entity.getMonedaDestino())
                .tipoCambio(entity.getTipoCambio())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }
}
```

**✅ Prueba:** 
1. Compila
2. Ejecuta la app
3. Prueba con Postman: `GET http://localhost:8080/exchange-rate?origen=PEN&destino=USD`

---

## 🎯 Flujo Completo

```
Cliente → GET /exchange-rate?origen=PEN&destino=USD
    ↓
ExchangeRateController.findByMonedas()
    ↓
ExchangeRateService.findByMonedas()
    ↓
ExchangeRateRepository.findByMonedaOrigenAndMonedaDestino()
    ↓
Base de Datos (H2)
    ↓
ExchangeRate (Entity)
    ↓
ExchangeRateResponse (DTO)
    ↓
Cliente recibe JSON
```

---

## 🧪 Cómo Probar

### 1. Primero, crea un dato de prueba

**Opción A: Directamente en la BD (temporal)**
```sql
INSERT INTO exchange_rates (moneda_origen, moneda_destino, tipo_cambio, fecha_actualizacion)
VALUES ('PEN', 'USD', 0.27, CURRENT_TIMESTAMP);
```

**Opción B: Con otro endpoint POST (mejor)**
Crea primero el endpoint POST para crear tipos de cambio, luego prueba el GET.

### 2. Prueba el endpoint

**Con cURL:**
```bash
curl "http://localhost:8080/exchange-rate?origen=PEN&destino=USD"
```

**Con Postman:**
- Método: GET
- URL: `http://localhost:8080/exchange-rate?origen=PEN&destino=USD`

**Resultado esperado:**
```json
{
  "id": 1,
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "tipoCambio": 0.27,
  "fechaActualizacion": "2025-12-09T14:00:00"
}
```

---

## 🐛 Si No Funciona

### Error: "Table not found"
- Verifica que `schema.sql` esté en `src/main/resources/`
- Verifica que `DatabaseInitializer` esté configurado

### Error: "No data found"
- Crea un registro primero (INSERT o endpoint POST)

### Error: "404 Not Found"
- Verifica que la app esté corriendo
- Verifica la URL: `http://localhost:8080/exchange-rate?origen=PEN&destino=USD`

### Error: "500 Internal Server Error"
- Revisa los logs de la aplicación
- Verifica que todas las dependencias estén en `pom.xml`

---

## ✅ Checklist de Este Ejemplo

- [ ] Entidad creada y compila
- [ ] Schema SQL creado
- [ ] Repositorio creado y compila
- [ ] Servicio creado y compila
- [ ] DTO creado y compila
- [ ] Controlador creado y compila
- [ ] App inicia sin errores
- [ ] Endpoint responde correctamente

---

## 🎓 Qué Aprendiste

1. ✅ Cómo crear una entidad con R2DBC
2. ✅ Cómo crear un repositorio reactivo
3. ✅ Cómo crear un servicio que usa el repositorio
4. ✅ Cómo crear un DTO para la respuesta
5. ✅ Cómo crear un controlador WebFlux
6. ✅ Cómo encadenar operaciones con `map()`
7. ✅ Cómo probar un endpoint

---

## 🚀 Siguiente Paso

Ahora que tienes el GET funcionando, intenta crear:
- `POST /exchange-rate` (crear tipo de cambio)
- `PUT /exchange-rate/{id}` (actualizar)
- `DELETE /exchange-rate/{id}` (eliminar)

¡Sigue el mismo patrón! 🎯


