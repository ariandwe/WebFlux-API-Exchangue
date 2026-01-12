# ⚡ Resumen Rápido: Entrevista NTT Data - WebFlux

## 🎯 Respuestas Clave (30 segundos cada una)

### 1. ¿Qué es WebFlux?
"Framework reactivo de Spring para aplicaciones no bloqueantes. Lo usé para alta escalabilidad y concurrencia en mi servicio de tipos de cambio."

### 2. Mono vs Flux
"Mono = 0 o 1 resultado. Flux = 0 o N resultados. En mi proyecto: Mono para buscar uno, Flux para listar todos."

### 3. flatMap vs map
"map transforma el valor. flatMap transforma y aplana (cuando retornas otro Mono). Si retornas Mono, usa flatMap."

### 4. ¿Por qué R2DBC?
"R2DBC es reactivo, JPA es bloqueante. Necesitaba stack completo reactivo: WebFlux + R2DBC, todo no bloqueante."

### 5. ¿Cómo funciona la suscripción?
"El método define qué hacer (retorna Mono). WebFlux se suscribe automáticamente cuando hay request HTTP y ejecuta."

### 6. ¿Cómo manejas errores?
"switchIfEmpty para cuando no hay datos, onErrorResume para errores específicos, GlobalExceptionHandler para manejo global."

### 7. ¿Cómo implementaste seguridad?
"JwtAuthenticationFilter con WebFilter (reactivo), SecurityConfig con @EnableWebFluxSecurity, ReactiveSecurityContextHolder para obtener usuario."

### 8. ¿Cómo probaste?
"StepVerifier de Reactor Test. Verifico que el Mono emite el valor esperado y se completa correctamente."

---

## 📋 Checklist Pre-Entrevista

- [ ] Puedo explicar qué es WebFlux
- [ ] Sé la diferencia Mono/Flux
- [ ] Sé la diferencia flatMap/map
- [ ] Puedo explicar por qué R2DBC
- [ ] Entiendo la suscripción
- [ ] Sé cómo manejar errores
- [ ] Puedo explicar seguridad reactiva
- [ ] Sé cómo probar código reactivo
- [ ] Conozco ventajas/desventajas
- [ ] Puedo explicar mi arquitectura

---

## 💡 Tips

1. **Siempre menciona tu proyecto**: "En mi proyecto de tipos de cambio..."
2. **Da ejemplos de código**: "Usé flatMap() para encadenar..."
3. **Sé honesto**: Si no sabes, dilo pero muestra cómo lo investigarías
4. **Habla de desafíos**: Muestra que aprendiste resolviendo problemas

---

## 🎯 Preguntas Probables (Orden de Frecuencia)

1. ⭐⭐⭐ ¿Qué es WebFlux?
2. ⭐⭐⭐ Mono vs Flux
3. ⭐⭐ flatMap vs map
4. ⭐⭐ ¿Por qué R2DBC?
5. ⭐⭐ ¿Cómo funciona la suscripción?
6. ⭐ ¿Cómo manejas errores?
7. ⭐ ¿Cómo implementaste seguridad?
8. ⭐ ¿Cómo probaste?
9. ⭐ Ventajas/desventajas
10. ⭐ Arquitectura del proyecto

---

¡Éxito! 🚀



