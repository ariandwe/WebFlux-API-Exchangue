# ⚡ Resumen Rápido: WebFlux en el Proyecto

## 🎯 Respuesta de 30 segundos

"Usé **Spring WebFlux** en todo el proyecto. Es completamente reactivo y no bloqueante. Los controladores retornan `Mono<T>`, los servicios encadenan operaciones con `flatMap()`, y los repositorios usan R2DBC. La ejecución es diferida - los métodos definen qué hacer, pero la suscripción y ejecución real ocurre cuando WebFlux lo decide."

---

## 🏗️ Capas con WebFlux

```
┌─────────────────────────────────────┐
│  1. CONTROLLERS (WebFlux)           │
│     Retorna: Mono<T> o Flux<T>      │
│     Recibe: Mono<Request>            │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│  2. SERVICES (Reactivos)            │
│     Encadena con: flatMap(), map()   │
│     Retorna: Mono<T> o Flux<T>       │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│  3. REPOSITORIES (R2DBC)            │
│     Extiende: ReactiveCrudRepository│
│     Retorna: Mono<T> o Flux<T>      │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│  4. DATABASE (H2 + R2DBC)          │
│     Conexión reactiva, no bloqueante│
└─────────────────────────────────────┘
```

---

## 💡 Conceptos Clave

### **Mono vs Flux**
- `Mono<T>` = 0 o 1 resultado
- `Flux<T>` = 0 o N resultados

### **Suscripción**
- El método **define** qué hacer
- La **suscripción** ejecuta (automática en WebFlux)

### **Operadores**
- `flatMap()` = Encadena operaciones (retorna Mono)
- `map()` = Transforma valor (retorna valor)
- `switchIfEmpty()` = Si está vacío, hacer otra cosa
- `thenReturn()` = Ejecutar y retornar otro valor

---

## 📝 Ejemplo Concreto del Código

```java
// CONTROLLER
public Mono<ApplyExchangeResponse> applyExchange(
    @RequestBody Mono<ApplyExchangeRequest> request) {
    return request.flatMap(service::applyExchange);
}

// SERVICE
public Mono<ApplyExchangeResponse> applyExchange(...) {
    return repository.findByMonedaOrigenAndMonedaDestino(...)
        .switchIfEmpty(Mono.error(...))
        .flatMap(rate -> {
            // Calcular
            return auditService.log(...)
                .thenReturn(response);
        });
}

// REPOSITORY
Mono<ExchangeRate> findByMonedaOrigenAndMonedaDestino(...);
```

---

## 🎯 Respuestas Rápidas

**"¿Por qué WebFlux?"**
→ Mejor escalabilidad, no bloqueante, stack completo reactivo

**"¿Dónde se aplica?"**
→ Todas las capas: Controllers, Services, Repositories, Security

**"¿Cómo funciona la suscripción?"**
→ Automática. El método define, WebFlux ejecuta al recibir request

**"¿Diferencia con MVC?"**
→ MVC bloquea threads, WebFlux no. MVC usa Tomcat, WebFlux usa Netty.

---

## ✅ Checklist

- [ ] Mono = 0 o 1, Flux = 0 o N
- [ ] Suscripción ejecuta, método define
- [ ] flatMap encadena, map transforma
- [ ] R2DBC es reactivo, JPA es bloqueante
- [ ] Netty es el servidor reactivo
- [ ] Todo el stack es no bloqueante




