# 🎯 Preguntas de Entrevista NTT Data: WebFlux (2 años experiencia)

## 📋 Contexto del Puesto
- **Empresa:** NTT Data
- **Rol:** Desarrollador Java - Creador de Microservicios
- **Experiencia:** 2 años
- **Enfoque:** Spring WebFlux, Programación Reactiva

---

## 🔥 PREGUNTAS NIVEL BÁSICO (Primera Ronda)

### 1. "¿Qué es Spring WebFlux y cuándo lo usarías?"

**Respuesta esperada:**
"Spring WebFlux es el framework reactivo de Spring para construir aplicaciones no bloqueantes. Lo usaría cuando necesito:
- Alta concurrencia y escalabilidad
- APIs que manejan muchos requests simultáneos
- Microservicios que necesitan ser eficientes con recursos
- Integración con sistemas reactivos (como bases de datos R2DBC)

En mi proyecto, lo usé porque necesitaba un servicio de tipos de cambio que pudiera manejar muchas conversiones simultáneas sin bloquear threads."

**Puntos clave:**
- ✅ Menciona "no bloqueante"
- ✅ Habla de escalabilidad
- ✅ Da un ejemplo concreto

---

### 2. "¿Cuál es la diferencia entre Mono y Flux?"

**Respuesta esperada:**
"Mono representa un stream que emite 0 o 1 elemento. Flux representa un stream que emite 0 o N elementos.

En mi proyecto:
- `Mono<ExchangeRate>` para buscar un tipo de cambio específico
- `Flux<ExchangeRate>` para listar todos los tipos de cambio
- `Mono<ApplyExchangeResponse>` para una conversión única

La diferencia práctica es que Mono es para operaciones que retornan un solo resultado, y Flux para colecciones o streams de datos."

**Puntos clave:**
- ✅ Definición clara
- ✅ Ejemplo del proyecto
- ✅ Cuándo usar cada uno

---

### 3. "¿Qué es la programación reactiva?"

**Respuesta esperada:**
"La programación reactiva es un paradigma que trabaja con streams de datos y propagación de cambios. En WebFlux:
- Los métodos retornan `Mono` o `Flux` (promesas de datos)
- Las operaciones se encadenan sin bloquear threads
- La ejecución es diferida hasta la suscripción
- Todo es asíncrono y no bloqueante

En mi proyecto, cuando aplico un tipo de cambio, encadeno varias operaciones: buscar el tipo de cambio, calcular el monto, y guardar auditoría - todo sin bloquear threads."

**Puntos clave:**
- ✅ Menciona streams y propagación
- ✅ Habla de no bloqueante
- ✅ Ejemplo práctico

---

## 🎯 PREGUNTAS NIVEL INTERMEDIO (Segunda Ronda)

### 4. "¿Cuál es la diferencia entre flatMap y map?"

**Respuesta esperada:**
"`map` transforma el valor dentro del Mono/Flux. `flatMap` transforma y 'aplana' el resultado (cuando retornas otro Mono/Flux).

Ejemplo de mi proyecto:

```java
// map: Transforma ExchangeRate a ExchangeRateResponse
return repository.findById(id)
    .map(this::toResponse); // Retorna Mono<ExchangeRateResponse>

// flatMap: Encadena operaciones que retornan Mono
return repository.findByMonedaOrigenAndMonedaDestino(...)
    .flatMap(rate -> auditService.log(...)); // Retorna Mono<AuditLog>
```

Regla: Si retornas un valor simple, usa `map`. Si retornas otro `Mono` o `Flux`, usa `flatMap`."

**Puntos clave:**
- ✅ Diferencia técnica clara
- ✅ Ejemplo de código
- ✅ Regla práctica

---

### 5. "¿Cómo manejas errores en WebFlux?"

**Respuesta esperada:**
"En WebFlux manejo errores de varias formas:

1. **switchIfEmpty**: Si no hay datos, lanzar error
```java
return repository.findById(id)
    .switchIfEmpty(Mono.error(new NotFoundException()));
```

2. **onErrorResume**: Manejar errores específicos
```java
return service.findByMonedas(...)
    .onErrorResume(NotFoundException.class, 
        e -> Mono.just(defaultValue));
```

3. **GlobalExceptionHandler**: Manejo global de excepciones
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<...>> handle(...) {
        // Retornar error estructurado
    }
}
```

En mi proyecto, uso `switchIfEmpty` para cuando no encuentro un tipo de cambio, y un `GlobalExceptionHandler` para manejar todos los errores de forma consistente."

**Puntos clave:**
- ✅ Menciona múltiples formas
- ✅ Ejemplos de código
- ✅ Ejemplo del proyecto

---

### 6. "¿Por qué usaste R2DBC en lugar de JPA?"

**Respuesta esperada:**
"R2DBC es la alternativa reactiva a JPA. Las diferencias clave:

- **JPA/Hibernate**: Es bloqueante, usa threads que esperan I/O
- **R2DBC**: Es reactivo, no bloquea threads

En mi proyecto necesitaba un stack completamente reactivo:
- WebFlux (servidor reactivo)
- R2DBC (repositorios reactivos)
- Todo no bloqueante end-to-end

Con JPA, aunque el controlador fuera reactivo, la capa de datos seguiría bloqueando threads, perdiendo los beneficios de WebFlux.

R2DBC retorna `Mono<T>` o `Flux<T>`, permitiendo que el thread se libere mientras espera la respuesta de la BD."

**Puntos clave:**
- ✅ Diferencia técnica clara
- ✅ Razón de negocio (stack completo reactivo)
- ✅ Beneficio práctico

---

### 7. "¿Cómo funciona la suscripción en WebFlux?"

**Respuesta esperada:**
"La suscripción es cuando realmente se ejecuta la cadena reactiva. En WebFlux:

1. **Definición**: El método retorna un `Mono<T>` (aún no ejecutado)
```java
public Mono<ExchangeRate> findById(Long id) {
    return repository.findById(id); // Solo define, no ejecuta
}
```

2. **Suscripción**: WebFlux se suscribe automáticamente cuando hay un request HTTP
3. **Ejecución**: Se ejecuta toda la cadena reactiva
4. **Resultado**: Se retorna al cliente

En mi proyecto, cuando el controlador retorna `Mono<ApplyExchangeResponse>`, WebFlux se suscribe automáticamente y ejecuta: buscar tipo de cambio → calcular monto → guardar auditoría.

La suscripción es automática en WebFlux, no necesito llamar `.subscribe()` manualmente en los controladores."

**Puntos clave:**
- ✅ Explica definición vs ejecución
- ✅ Menciona suscripción automática
- ✅ Ejemplo del flujo

---

## 🚀 PREGUNTAS NIVEL AVANZADO (Tercera Ronda)

### 8. "¿Cómo implementaste seguridad con WebFlux?"

**Respuesta esperada:**
"Implementé seguridad reactiva con Spring Security WebFlux:

1. **JwtAuthenticationFilter**: Implementa `WebFilter` (no `Filter` de servlet)
```java
public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    // Validar token
    return chain.filter(exchange)
        .contextWrite(ReactiveSecurityContextHolder
            .withAuthentication(authentication));
}
```

2. **SecurityConfig**: Configuración reactiva
```java
@EnableWebFluxSecurity
public class SecurityConfig {
    public SecurityWebFilterChain securityWebFilterChain(...) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/login").permitAll()
                .anyExchange().authenticated()
            )
            .build();
    }
}
```

3. **ReactiveSecurityContextHolder**: Para obtener el usuario autenticado
```java
return ReactiveSecurityContextHolder.getContext()
    .map(SecurityContext::getAuthentication)
    .map(Authentication::getName);
```

Todo es reactivo, desde el filtro hasta el contexto de seguridad."

**Puntos clave:**
- ✅ Menciona WebFilter (no Filter)
- ✅ Habla de ReactiveSecurityContextHolder
- ✅ Ejemplos de código

---

### 9. "¿Cómo probaste tu código reactivo?"

**Respuesta esperada:**
"Usé `StepVerifier` de Reactor Test para probar código reactivo:

```java
@Test
void testApplyExchange_Success() {
    when(repository.findByMonedaOrigenAndMonedaDestino(...))
        .thenReturn(Mono.just(exchangeRate));
    
    StepVerifier.create(service.applyExchange(request))
        .expectNextMatches(response -> 
            response.getMontoConvertido().equals(new BigDecimal("27.00"))
        )
        .verifyComplete();
}
```

StepVerifier permite:
- Verificar que el Mono emite el valor esperado
- Verificar errores con `expectError()`
- Verificar que se completa con `verifyComplete()`

También uso mocks de Mockito para los repositorios, pero siempre retornando `Mono` o `Flux`."

**Puntos clave:**
- ✅ Menciona StepVerifier
- ✅ Ejemplo de código
- ✅ Explica qué verifica

---

### 10. "¿Cuáles son las ventajas y desventajas de WebFlux?"

**Respuesta esperada:**
"**Ventajas:**
- Alta escalabilidad: Maneja muchos requests con pocos threads
- Eficiencia de recursos: No bloquea threads esperando I/O
- Stack completo reactivo: Desde servidor hasta BD
- Ideal para microservicios con alta concurrencia

**Desventajas:**
- Curva de aprendizaje: Programación reactiva es diferente
- Debugging más complejo: Stack traces pueden ser largos
- No todos los ecosistemas son reactivos: Algunas librerías son bloqueantes
- Overhead para operaciones simples: Para apps simples, MVC puede ser suficiente

En mi proyecto, las ventajas superaron las desventajas porque necesitaba manejar muchas conversiones simultáneas. Para un CRUD simple, MVC habría sido más simple."

**Puntos clave:**
- ✅ Balanceado (ventajas y desventajas)
- ✅ Honesto sobre complejidad
- ✅ Contexto de cuándo usar

---

## 🎯 PREGUNTAS SOBRE MICROSERVICIOS

### 11. "¿Cómo integrarías este microservicio con otros?"

**Respuesta esperada:**
"Para integrar microservicios con WebFlux, usaría:

1. **WebClient** (reactivo) en lugar de RestTemplate (bloqueante)
```java
@Autowired
private WebClient webClient;

public Mono<ExternalData> getExternalData() {
    return webClient.get()
        .uri("http://other-service/api/data")
        .retrieve()
        .bodyToMono(ExternalData.class);
}
```

2. **Circuit Breaker** con Resilience4j para manejar fallos
3. **Service Discovery** (Eureka, Consul) para descubrir servicios
4. **API Gateway** (Spring Cloud Gateway) que también es reactivo

Todo el stack de integración sería reactivo para mantener los beneficios de WebFlux."

**Puntos clave:**
- ✅ Menciona WebClient
- ✅ Habla de circuit breaker
- ✅ Stack completo reactivo

---

### 12. "¿Cómo manejarías la concurrencia en este microservicio?"

**Respuesta esperada:**
"WebFlux maneja la concurrencia automáticamente con su modelo event-loop:

- **Netty** (servidor) usa pocos threads para muchos requests
- Cada operación I/O es no bloqueante
- Los threads se reutilizan eficientemente

En mi proyecto, cuando múltiples usuarios aplican conversiones simultáneamente:
- Cada request se maneja de forma asíncrona
- No hay bloqueo de threads
- El servidor puede manejar miles de requests concurrentes

Para operaciones críticas (como actualizar tipos de cambio), podría agregar locks reactivos o usar transacciones reactivas con R2DBC."

**Puntos clave:**
- ✅ Explica event-loop
- ✅ Menciona no bloqueante
- ✅ Habla de escalabilidad

---

## 💡 PREGUNTAS DE ARQUITECTURA

### 13. "¿Cómo estructuraste las capas en tu proyecto?"

**Respuesta esperada:**
"Estructuré el proyecto en capas siguiendo arquitectura limpia:

1. **Entity**: Entidades de dominio (ExchangeRate, AuditLog)
2. **Repository**: Acceso a datos con R2DBC
3. **Service**: Lógica de negocio reactiva
4. **DTO**: Objetos de transferencia (Request/Response)
5. **Controller**: Endpoints REST
6. **Security**: Configuración de seguridad reactiva
7. **Exception**: Manejo global de errores

Cada capa tiene responsabilidades claras:
- Repositorios solo acceden a datos
- Servicios contienen lógica de negocio
- Controladores solo orquestan

Todo el flujo es reactivo: Controller → Service → Repository, todos retornando Mono/Flux."

**Puntos clave:**
- ✅ Estructura clara
- ✅ Separación de responsabilidades
- ✅ Flujo reactivo end-to-end

---

### 14. "¿Cómo implementaste la auditoría de forma reactiva?"

**Respuesta esperada:**
"Implementé auditoría de forma reactiva encadenando operaciones:

```java
public Mono<ApplyExchangeResponse> applyExchange(...) {
    return repository.findByMonedaOrigenAndMonedaDestino(...)
        .flatMap(exchangeRate -> {
            // Calcular monto
            BigDecimal montoConvertido = calculate(...);
            
            // Encadenar auditoría
            return getCurrentUsername()
                .flatMap(username -> 
                    auditService.logExchangeOperation(...)
                )
                .thenReturn(response); // Retornar después de guardar
        });
}
```

La auditoría se guarda de forma asíncrona sin bloquear la respuesta al cliente. Si la auditoría falla, podría usar `doOnError` para logging, pero no afectar la respuesta principal."

**Puntos clave:**
- ✅ Encadenamiento reactivo
- ✅ No bloquea la respuesta
- ✅ Manejo de errores

---

## 🎤 PREGUNTAS DE COMPORTAMIENTO

### 15. "¿Qué desafíos enfrentaste con WebFlux?"

**Respuesta esperada:**
"Los principales desafíos fueron:

1. **Curva de aprendizaje**: Pensar de forma reactiva es diferente. Al principio quería usar `.block()` para hacer código bloqueante, pero aprendí a encadenar con `flatMap()`.

2. **Debugging**: Los stack traces pueden ser largos. Aprendí a usar logging estratégico y a entender el flujo reactivo.

3. **H2 Console**: No funciona con WebFlux. Solucioné creando endpoints REST para consultar datos.

4. **Tests**: Aprender a usar `StepVerifier` y mocks reactivos fue un desafío inicial.

La solución fue practicar mucho, leer documentación de Reactor, y construir el proyecto paso a paso, probando cada capa."

**Puntos clave:**
- ✅ Honesto sobre desafíos
- ✅ Menciona soluciones
- ✅ Actitud de aprendizaje

---

## ✅ CHECKLIST DE PREPARACIÓN

Antes de la entrevista, asegúrate de poder explicar:

- [ ] Qué es WebFlux y por qué lo usas
- [ ] Diferencia entre Mono y Flux
- [ ] Diferencia entre flatMap y map
- [ ] Por qué R2DBC en lugar de JPA
- [ ] Cómo funciona la suscripción
- [ ] Cómo manejas errores
- [ ] Cómo implementaste seguridad
- [ ] Cómo probaste código reactivo
- [ ] Ventajas y desventajas de WebFlux
- [ ] Arquitectura de tu proyecto
- [ ] Desafíos que enfrentaste

---

## 🎯 TIPS PARA LA ENTREVISTA

1. **Siempre da ejemplos de tu proyecto**: "En mi proyecto de tipos de cambio..."
2. **Menciona código específico**: "Usé `flatMap()` para encadenar..."
3. **Sé honesto**: Si no sabes algo, dilo, pero muestra cómo lo investigarías
4. **Habla de desafíos**: Muestra que aprendiste y resolviste problemas
5. **Conecta con microservicios**: Siempre relaciona con el contexto del puesto

---

## 🚀 PREGUNTA BONUS (Si quieren profundizar)

### "¿Cómo optimizarías este microservicio para producción?"

**Respuesta esperada:**
"Para producción optimizaría:

1. **Connection Pooling**: Configurar pool de conexiones R2DBC
2. **Caching**: Redis reactivo para tipos de cambio frecuentes
3. **Monitoring**: Métricas con Micrometer, tracing con Sleuth
4. **Rate Limiting**: Limitar requests por usuario
5. **Circuit Breaker**: Para llamadas a servicios externos
6. **Health Checks**: Endpoints de salud reactivos
7. **Logging estructurado**: Para debugging en producción
8. **Performance Testing**: Cargar el servicio y medir throughput

Todo manteniendo el modelo reactivo para no perder los beneficios de WebFlux."

---

¡Éxito en tu entrevista con NTT Data! 🎯



