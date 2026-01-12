# 🎯 Guía para Entrevista: WebFlux en el Proyecto

## 📋 Respuesta Corta (30 segundos)

"Sí, usé **Spring WebFlux** en todo el proyecto. Es una arquitectura completamente reactiva y no bloqueante. En lugar de Spring MVC, usé WebFlux porque permite mejor escalabilidad y manejo de concurrencia. Todo el stack es reactivo: desde los controladores que reciben `Mono<T>`, pasando por los servicios que retornan `Mono` o `Flux`, hasta los repositorios R2DBC que son reactivos. La ejecución es diferida - los métodos definen qué se va a hacer, pero la suscripción y ejecución real ocurre cuando el framework lo decide."

---

## 🏗️ Arquitectura en Capas con WebFlux

### **1. Capa de Controladores (Controllers)**

**Ubicación:** `controllers/ExchangeController.java`

```java
@RestController
public class ExchangeController {
    
    // ✅ WebFlux: El método retorna Mono<T>
    // ✅ WebFlux: Recibe Mono<Request> (no bloquea)
    public Mono<ApplyExchangeResponse> applyExchange(
        @RequestBody Mono<ApplyExchangeRequest> request) {
        
        // flatMap = operador reactivo para encadenar operaciones
        return request.flatMap(exchangeApplyService::applyExchange);
    }
}
```

**¿Por qué WebFlux aquí?**
- El controlador **NO bloquea** el thread mientras espera la respuesta
- Puede manejar **miles de requests concurrentes** con pocos threads
- Usa **Netty** como servidor (no Tomcat como MVC)

---

### **2. Capa de Servicios (Services)**

**Ubicación:** `service/ExchangeApplyService.java`

```java
@Service
public class ExchangeApplyService {
    
    // ✅ WebFlux: Retorna Mono<T> (promesa de un resultado)
    public Mono<ApplyExchangeResponse> applyExchange(ApplyExchangeRequest request) {
        
        // 1. Buscar tipo de cambio (reactivo, no bloquea)
        return exchangeRateRepository
                .findByMonedaOrigenAndMonedaDestino(...)
                
                // 2. Si no existe, lanzar error (reactivo)
                .switchIfEmpty(Mono.error(...))
                
                // 3. Transformar el resultado (reactivo)
                .flatMap(exchangeRate -> {
                    // Calcular monto convertido
                    BigDecimal montoConvertido = calculateConvertedAmount(...);
                    
                    // 4. Registrar auditoría (reactivo, encadenado)
                    return getCurrentUsername()
                            .flatMap(username -> 
                                auditService.logExchangeOperation(...)
                            )
                            .thenReturn(response); // Retornar después de guardar
                });
    }
}
```

**Conceptos clave:**
- `Mono<T>` = Promesa de **0 o 1** resultado
- `Flux<T>` = Promesa de **0 o N** resultados
- `flatMap()` = Encadena operaciones reactivas (no bloquea)
- `switchIfEmpty()` = Maneja el caso cuando no hay datos
- `thenReturn()` = Ejecuta algo y luego retorna otro valor

**¿Por qué WebFlux aquí?**
- Las operaciones se **encadenan** sin bloquear threads
- Si la BD tarda, el thread se libera y puede atender otros requests
- Todo es **asíncrono** y **no bloqueante**

---

### **3. Capa de Repositorios (Repositories)**

**Ubicación:** `repository/ExchangeRateRepository.java`

```java
@Repository
// ✅ WebFlux: Extiende ReactiveCrudRepository (no JpaRepository)
public interface ExchangeRateRepository 
    extends ReactiveCrudRepository<ExchangeRate, Long> {
    
    // ✅ WebFlux: Retorna Mono<T> (no bloquea)
    @Query("SELECT * FROM exchange_rates WHERE ...")
    Mono<ExchangeRate> findByMonedaOrigenAndMonedaDestino(...);
}
```

**Diferencias clave:**
- **MVC:** `JpaRepository` → Retorna `Optional<T>` o `List<T>` (bloqueante)
- **WebFlux:** `ReactiveCrudRepository` → Retorna `Mono<T>` o `Flux<T>` (no bloqueante)

**¿Por qué R2DBC?**
- **JPA/Hibernate** es bloqueante (usa threads)
- **R2DBC** es reactivo (no bloquea threads)
- Compatible con WebFlux end-to-end

---

### **4. Capa de Seguridad (Security)**

**Ubicación:** `security/JwtAuthenticationFilter.java`

```java
@Component
public class JwtAuthenticationFilter implements WebFilter {
    
    // ✅ WebFlux: Implementa WebFilter (no Filter de servlet)
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        
        // Validar token (reactivo)
        String token = getTokenFromRequest(exchange);
        
        if (jwtUtil.validateToken(token, username)) {
            // ✅ WebFlux: Establecer contexto de forma reactiva
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder
                        .withAuthentication(authentication));
        }
        
        return chain.filter(exchange);
    }
}
```

**¿Por qué WebFlux aquí?**
- `WebFilter` es reactivo (no `Filter` de servlet)
- `ReactiveSecurityContextHolder` maneja el contexto de forma no bloqueante
- Compatible con el modelo reactivo

---

## 🔄 Flujo Completo de una Petición

```
1. Cliente → POST /exchange/apply
   ↓
2. Netty (servidor reactivo) recibe el request
   ↓
3. JwtAuthenticationFilter (WebFilter reactivo)
   ↓
4. ExchangeController.applyExchange()
   - Recibe: Mono<ApplyExchangeRequest>
   - Retorna: Mono<ApplyExchangeResponse>
   ↓
5. ExchangeApplyService.applyExchange()
   - Retorna: Mono<ApplyExchangeResponse>
   - Encadena operaciones con flatMap()
   ↓
6. ExchangeRateRepository.findByMonedaOrigenAndMonedaDestino()
   - Retorna: Mono<ExchangeRate>
   - NO BLOQUEA el thread mientras consulta BD
   ↓
7. AuditService.logExchangeOperation()
   - Retorna: Mono<AuditLog>
   - Guarda en BD de forma reactiva
   ↓
8. Response → Cliente recibe el resultado
```

**Punto clave:** En ningún momento se bloquea un thread esperando I/O.

---

## 💡 Conceptos Clave para la Entrevista

### **1. Mono vs Flux**

```java
// Mono = 0 o 1 resultado
Mono<ExchangeRate> rate = repository.findById(1L);

// Flux = 0 o N resultados
Flux<ExchangeRate> allRates = repository.findAll();
```

### **2. Suscripción (Subscription)**

**Tu entendimiento es correcto:**
- El método **define** qué se va a hacer
- La **suscripción** es cuando realmente se ejecuta

```java
// Esto NO ejecuta nada todavía
Mono<ExchangeRate> rate = repository.findById(1L);

// La suscripción ocurre cuando WebFlux lo decide
// (normalmente cuando retornas el Mono al controlador)
```

**En nuestro código:**
```java
// El controlador retorna el Mono
// WebFlux se suscribe automáticamente cuando hay un request
return request.flatMap(exchangeApplyService::applyExchange);
```

### **3. Operadores Reactivos**

```java
// flatMap = Transforma y encadena
.flatMap(exchangeRate -> {
    return auditService.log(...);
})

// switchIfEmpty = Si está vacío, hacer otra cosa
.switchIfEmpty(Mono.error(...))

// thenReturn = Ejecutar algo y retornar otro valor
.thenReturn(response)

// map = Transformar el valor
.map(rate -> rate.getTipoCambio())
```

### **4. No Bloqueante (Non-Blocking)**

**MVC (Bloqueante):**
```java
// Bloquea el thread esperando la BD
ExchangeRate rate = repository.findById(1L).get(); // ❌ BLOQUEA
```

**WebFlux (No Bloqueante):**
```java
// NO bloquea, el thread se libera
Mono<ExchangeRate> rate = repository.findById(1L); // ✅ NO BLOQUEA
```

---

## 🎯 Respuestas para Preguntas Comunes

### **"¿Por qué elegiste WebFlux sobre MVC?"**

"Elegí WebFlux porque:
1. **Mejor escalabilidad:** Puede manejar más requests concurrentes con menos recursos
2. **No bloqueante:** Los threads no se bloquean esperando I/O
3. **Stack completo reactivo:** Desde el controlador hasta la BD (R2DBC)
4. **Adecuado para APIs:** Perfecto para servicios REST que necesitan alta concurrencia"

### **"¿Dónde se aplica WebFlux en tu proyecto?"**

"WebFlux se aplica en **todas las capas**:
1. **Controladores:** Retornan `Mono<T>` o `Flux<T>`
2. **Servicios:** Encadenan operaciones con `flatMap()`, `map()`, etc.
3. **Repositorios:** Usan R2DBC con `ReactiveCrudRepository`
4. **Seguridad:** `WebFilter` y `ReactiveSecurityContextHolder`
5. **Servidor:** Netty (no Tomcat)"

### **"¿Cómo funciona la suscripción?"**

"La suscripción es automática en WebFlux. Cuando un controlador retorna un `Mono<T>`, WebFlux:
1. **Recibe** el Mono (aún no ejecutado)
2. **Se suscribe** cuando hay un request HTTP
3. **Ejecuta** la cadena de operaciones reactivas
4. **Retorna** el resultado al cliente

El método solo **define** qué hacer, la **ejecución** ocurre en la suscripción."

### **"¿Cuál es la diferencia entre flatMap y map?"**

```java
// map = Transforma el valor dentro del Mono
Mono<String> name = repository.findById(1L)
    .map(rate -> rate.getMonedaOrigen()); // String dentro de Mono

// flatMap = Transforma y "aplanar" (retorna otro Mono)
Mono<AuditLog> log = repository.findById(1L)
    .flatMap(rate -> auditService.save(...)); // Mono dentro de Mono → Mono
```

**Regla:** Si retornas un `Mono`, usa `flatMap`. Si retornas un valor simple, usa `map`.

---

## 📊 Comparación MVC vs WebFlux

| Aspecto | Spring MVC | Spring WebFlux |
|---------|-----------|----------------|
| **Servidor** | Tomcat (Servlet) | Netty (Reactor) |
| **Modelo** | Bloqueante | No Bloqueante |
| **Threads** | 1 thread por request | Pocos threads, muchos requests |
| **Repositorio** | JpaRepository | ReactiveCrudRepository |
| **Retorno** | `T`, `List<T>`, `Optional<T>` | `Mono<T>`, `Flux<T>` |
| **BD** | JPA/Hibernate | R2DBC |
| **Escalabilidad** | Limitada por threads | Alta (event-loop) |

---

## 🎤 Ejemplo de Respuesta Completa (2 minutos)

"En este proyecto implementé una arquitectura completamente reactiva con Spring WebFlux. 

**En la capa de controladores**, todos los endpoints retornan `Mono<T>` o `Flux<T>`. Por ejemplo, el endpoint de aplicar conversión recibe un `Mono<ApplyExchangeRequest>` y retorna un `Mono<ApplyExchangeResponse>`. Esto permite que el servidor Netty maneje miles de requests concurrentes sin bloquear threads.

**En la capa de servicios**, encadeno operaciones usando operadores reactivos como `flatMap()`, `switchIfEmpty()`, y `thenReturn()`. Por ejemplo, cuando aplico un tipo de cambio, primero busco el tipo de cambio en la BD de forma reactiva, luego calculo el monto convertido, y finalmente registro la auditoría - todo sin bloquear threads.

**En la capa de repositorios**, uso R2DBC con `ReactiveCrudRepository` en lugar de JPA. Esto permite que las consultas a la base de datos sean no bloqueantes. Los métodos retornan `Mono<T>` o `Flux<T>`, definiendo qué se va a hacer, pero la ejecución real ocurre cuando WebFlux se suscribe.

**La suscripción es automática**: cuando el controlador retorna un `Mono`, WebFlux se suscribe automáticamente y ejecuta toda la cadena reactiva. El método solo define la lógica, pero la ejecución es diferida hasta la suscripción.

**La ventaja principal** es la escalabilidad: puedo manejar muchos más requests concurrentes con menos recursos, porque los threads no se bloquean esperando I/O. Todo el stack, desde el servidor hasta la base de datos, es reactivo y no bloqueante."

---

## ✅ Checklist para la Entrevista

- [ ] Entiendo que WebFlux es no bloqueante
- [ ] Sé que Mono = 0 o 1 resultado, Flux = 0 o N resultados
- [ ] Comprendo que la suscripción ejecuta, el método solo define
- [ ] Puedo explicar dónde se aplica en cada capa
- [ ] Sé la diferencia entre flatMap y map
- [ ] Entiendo por qué usamos R2DBC en lugar de JPA
- [ ] Puedo explicar la ventaja de escalabilidad

---

## 🚀 Tips Adicionales

1. **Menciona Netty:** "Usamos Netty como servidor, que es un servidor reactivo basado en event-loop"

2. **Menciona el stack completo:** "Todo el stack es reactivo: Netty → WebFlux → R2DBC → H2"

3. **Da un ejemplo concreto:** "Por ejemplo, cuando aplico una conversión, busco el tipo de cambio, calculo el monto, y guardo auditoría - todo encadenado con flatMap sin bloquear threads"

4. **Menciona la escalabilidad:** "La principal ventaja es que puedo manejar miles de requests concurrentes con pocos threads"

¡Éxito en tu entrevista! 🎯




