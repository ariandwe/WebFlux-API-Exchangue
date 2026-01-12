# ⚡ Resumen: Orden de Desarrollo

## 🎯 Orden Lógico (Bottom-Up)

```
┌─────────────────────────────────┐
│  6. SEGURIDAD                    │ ← Último
│     (SecurityConfig, JWT)        │
└──────────────┬───────────────────┘
               │
┌──────────────▼───────────────────┐
│  5. CONTROLADORES                │
│     (Controllers)                │
└──────────────┬───────────────────┘
               │
┌──────────────▼───────────────────┐
│  4. DTOs                         │
│     (Request/Response)           │
└──────────────┬───────────────────┘
               │
┌──────────────▼───────────────────┐
│  3. SERVICIOS                    │
│     (Business Logic)             │
└──────────────┬───────────────────┘
               │
┌──────────────▼───────────────────┐
│  2. REPOSITORIOS                 │
│     (R2DBC Repositories)         │
└──────────────┬───────────────────┘
               │
┌──────────────▼───────────────────┐
│  1. ENTIDADES                    │ ← Primero
│     (Entity Classes)            │
└──────────────────────────────────┘
```

---

## 📝 Checklist Rápido

### ✅ Fase 1: Entidades
- [ ] ExchangeRate.java
- [ ] AuditLog.java
- [ ] @Table, @Column, @Id correctos

### ✅ Fase 2: Repositorios
- [ ] schema.sql creado
- [ ] DatabaseInitializer.java
- [ ] ExchangeRateRepository.java
- [ ] AuditLogRepository.java
- [ ] Extiende ReactiveCrudRepository

### ✅ Fase 3: Servicios
- [ ] ExchangeRateService.java
- [ ] AuditService.java
- [ ] Retornan Mono/Flux
- [ ] Usan flatMap() para encadenar

### ✅ Fase 4: DTOs
- [ ] ExchangeRateRequest.java
- [ ] ExchangeRateResponse.java
- [ ] Validaciones (@NotBlank, @NotNull)

### ✅ Fase 5: Controladores
- [ ] ExchangeRateController.java
- [ ] Endpoints GET, POST
- [ ] Retornan Mono/Flux
- [ ] Usan @Valid

### ✅ Fase 6: Seguridad
- [ ] JwtUtil.java
- [ ] SecurityConfig.java
- [ ] Endpoints protegidos

---

## 🎯 Regla de Oro

**"Construye de abajo hacia arriba, prueba cada capa antes de pasar a la siguiente"**

1. ✅ Crea entidades → Compila
2. ✅ Crea repositorios → Prueba que la BD funciona
3. ✅ Crea servicios → Prueba con tests simples
4. ✅ Crea DTOs → Compila
5. ✅ Crea controladores → Prueba con Postman
6. ✅ Agrega seguridad → Prueba autenticación

---

## 💡 Tips Rápidos

- **Empieza simple:** Un endpoint básico primero
- **Prueba seguido:** No esperes a terminar todo
- **Lee errores:** Te enseñan mucho
- **Experimenta:** Cambia valores y ve qué pasa

---

## 🐛 Errores Comunes

| Error | Solución |
|-------|----------|
| `ReactiveCrudRepository` no encontrado | Agrega `spring-boot-starter-data-r2dbc` |
| `Mono` no encontrado | `import reactor.core.publisher.Mono;` |
| Tabla no encontrada | Verifica `schema.sql` en `src/main/resources/` |
| Método debe retornar Mono/Flux | En WebFlux todo retorna Mono o Flux |

---

## 📚 Orden de Aprendizaje

1. **Día 1-2:** Entidades + Repositorios
2. **Día 3-4:** Servicios
3. **Día 5:** DTOs
4. **Día 6-7:** Controladores
5. **Día 8-9:** Seguridad
6. **Día 10:** Testing

---

¡Construye paso a paso y aprende en el camino! 🚀


