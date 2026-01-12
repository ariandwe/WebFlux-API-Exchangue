# 🚀 Guía: Construir el Proyecto desde Cero

## 📋 Orden de Desarrollo (Bottom-Up)

**Regla de oro:** Construye de abajo hacia arriba, probando cada capa antes de pasar a la siguiente.

```
1. ENTIDADES (Entity)          ← Empieza aquí
   ↓
2. REPOSITORIOS (Repository)    ← Luego esto
   ↓
3. SERVICIOS (Service)         ← Después esto
   ↓
4. DTOs (Data Transfer Objects) ← En paralelo con servicios
   ↓
5. CONTROLADORES (Controller)   ← Al final
   ↓
6. SEGURIDAD (Security)        ← Último paso
```

---

## 🎯 Fase 1: Entidades (Entity Layer)

### ¿Por qué empezar aquí?
Las entidades son la base de todo. Representan las tablas de la base de datos.

### Paso 1.1: Crear ExchangeRate

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

**¿Qué aprendes aquí?**
- `@Table`: Nombre de la tabla en BD
- `@Column`: Mapeo de campos (camelCase → snake_case)
- `@Id`: Clave primaria
- Lombok: `@Data`, `@Builder` reducen código boilerplate

### Paso 1.2: Crear AuditLog

**Archivo:** `src/main/java/com/example/demo/entity/AuditLog.java`

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
@Table("audit_logs")
public class AuditLog {
    
    @Id
    private Long id;
    
    @Column("usuario")
    private String usuario;
    
    @Column("moneda_origen")
    private String monedaOrigen;
    
    @Column("moneda_destino")
    private String monedaDestino;
    
    @Column("monto_inicial")
    private BigDecimal montoInicial;
    
    @Column("monto_convertido")
    private BigDecimal montoConvertido;
    
    @Column("tipo_cambio_aplicado")
    private BigDecimal tipoCambioAplicado;
    
    @Column("fecha")
    private LocalDateTime fecha;
}
```

**✅ Checklist Fase 1:**
- [ ] Entidades creadas
- [ ] Anotaciones correctas (@Table, @Column, @Id)
- [ ] Compila sin errores

---

## 🗄️ Fase 2: Repositorios (Repository Layer)

### ¿Por qué después de entidades?
Los repositorios trabajan con las entidades. Necesitas saber qué entidades tienes.

### Paso 2.1: Crear schema.sql

**Primero:** Crea el script SQL para las tablas.

**Archivo:** `src/main/resources/schema.sql`

```sql
-- Tabla para tipos de cambio
CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    moneda_origen VARCHAR(10) NOT NULL,
    moneda_destino VARCHAR(10) NOT NULL,
    tipo_cambio DECIMAL(20, 6) NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    UNIQUE(moneda_origen, moneda_destino)
);

-- Tabla para logs de auditoría
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL,
    moneda_origen VARCHAR(10) NOT NULL,
    moneda_destino VARCHAR(10) NOT NULL,
    monto_inicial DECIMAL(20, 2) NOT NULL,
    monto_convertido DECIMAL(20, 2) NOT NULL,
    tipo_cambio_aplicado DECIMAL(20, 6) NOT NULL,
    fecha TIMESTAMP NOT NULL
);
```

### Paso 2.2: Configurar DatabaseInitializer

**Archivo:** `src/main/java/com/example/demo/config/DatabaseInitializer.java`

```java
package com.example.demo.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializer {
    
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Bean
    public ConnectionFactoryInitializer initializer() {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
```

**¿Qué aprendes aquí?**
- Cómo inicializar la BD con R2DBC
- `ConnectionFactoryInitializer` ejecuta scripts SQL al iniciar

### Paso 2.3: Crear ExchangeRateRepository

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
    
    // Método personalizado con query
    @Query("SELECT * FROM exchange_rates WHERE moneda_origen = $1 AND moneda_destino = $2")
    Mono<ExchangeRate> findByMonedaOrigenAndMonedaDestino(
        String monedaOrigen, 
        String monedaDestino
    );
    
    // Verificar existencia
    @Query("SELECT COUNT(*) > 0 FROM exchange_rates WHERE moneda_origen = $1 AND moneda_destino = $2")
    Mono<Boolean> existsByMonedaOrigenAndMonedaDestino(
        String monedaOrigen, 
        String monedaDestino
    );
}
```

**¿Qué aprendes aquí?**
- `ReactiveCrudRepository`: Repositorio reactivo (no JpaRepository)
- `Mono<T>`: Retorna 0 o 1 resultado (reactivo)
- `@Query`: Queries personalizadas con parámetros posicionales ($1, $2)

### Paso 2.4: Crear AuditLogRepository

**Archivo:** `src/main/java/com/example/demo/repository/AuditLogRepository.java`

```java
package com.example.demo.repository;

import com.example.demo.entity.AuditLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository 
    extends ReactiveCrudRepository<AuditLog, Long> {
    // Solo CRUD básico, no necesita métodos personalizados
}
```

**✅ Checklist Fase 2:**
- [ ] schema.sql creado
- [ ] DatabaseInitializer configurado
- [ ] Repositorios creados
- [ ] Compila sin errores
- [ ] La app inicia y crea las tablas

**🧪 Prueba rápida:**
Ejecuta la app y verifica en los logs que no hay errores de BD.

---

## 🔧 Fase 3: Servicios (Service Layer)

### ¿Por qué después de repositorios?
Los servicios usan los repositorios para acceder a datos.

### Paso 3.1: Crear ExchangeRateService

**Archivo:** `src/main/java/com/example/demo/service/ExchangeRateService.java`

**Empieza simple, agrega complejidad después:**

```java
package com.example.demo.service;

import com.example.demo.entity.ExchangeRate;
import com.example.demo.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ExchangeRateService {
    
    @Autowired
    private ExchangeRateRepository repository;
    
    // 1. Método simple: Buscar por ID
    public Mono<ExchangeRate> findById(Long id) {
        return repository.findById(id);
    }
    
    // 2. Método simple: Listar todos
    public Flux<ExchangeRate> findAll() {
        return repository.findAll();
    }
    
    // 3. Método con lógica: Buscar por monedas
    public Mono<ExchangeRate> findByMonedas(String origen, String destino) {
        return repository.findByMonedaOrigenAndMonedaDestino(origen, destino);
    }
    
    // 4. Método con validación: Crear
    public Mono<ExchangeRate> create(ExchangeRate exchangeRate) {
        // Validar que no exista
        return repository
            .existsByMonedaOrigenAndMonedaDestino(
                exchangeRate.getMonedaOrigen(), 
                exchangeRate.getMonedaDestino()
            )
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("Ya existe"));
                }
                
                // Setear fecha y guardar
                exchangeRate.setFechaActualizacion(LocalDateTime.now());
                return repository.save(exchangeRate);
            });
    }
}
```

**¿Qué aprendes aquí?**
- `@Service`: Marca la clase como servicio
- `Mono<T>`: Para operaciones que retornan 1 resultado
- `Flux<T>`: Para operaciones que retornan múltiples resultados
- `flatMap()`: Encadena operaciones reactivas
- Validación antes de guardar

### Paso 3.2: Crear AuditService

**Archivo:** `src/main/java/com/example/demo/service/AuditService.java`

```java
package com.example.demo.service;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository repository;
    
    public Mono<AuditLog> logExchangeOperation(
            String usuario,
            String monedaOrigen,
            String monedaDestino,
            BigDecimal montoInicial,
            BigDecimal montoConvertido,
            BigDecimal tipoCambioAplicado) {
        
        AuditLog log = AuditLog.builder()
                .usuario(usuario)
                .monedaOrigen(monedaOrigen)
                .monedaDestino(monedaDestino)
                .montoInicial(montoInicial)
                .montoConvertido(montoConvertido)
                .tipoCambioAplicado(tipoCambioAplicado)
                .fecha(LocalDateTime.now())
                .build();
        
        return repository.save(log);
    }
}
```

**✅ Checklist Fase 3:**
- [ ] Servicios básicos creados
- [ ] Usan repositorios correctamente
- [ ] Retornan Mono/Flux
- [ ] Compila sin errores

**🧪 Prueba rápida:**
Crea un test simple para verificar que los servicios funcionan.

---

## 📦 Fase 4: DTOs (Data Transfer Objects)

### ¿Por qué ahora?
Los DTOs se usan entre controladores y servicios. Los necesitas antes de crear controladores.

### Paso 4.1: Crear DTOs de Request

**Archivo:** `src/main/java/com/example/demo/dto/ExchangeRateRequest.java`

```java
package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequest {
    
    @NotBlank(message = "La moneda origen es obligatoria")
    private String monedaOrigen;
    
    @NotBlank(message = "La moneda destino es obligatoria")
    private String monedaDestino;
    
    @NotNull(message = "El tipo de cambio es obligatorio")
    @Positive(message = "El tipo de cambio debe ser positivo")
    private BigDecimal tipoCambio;
}
```

**¿Qué aprendes aquí?**
- DTOs separan la API de las entidades
- Validaciones con `@NotBlank`, `@NotNull`, `@Positive`
- Protege tu API de datos inválidos

### Paso 4.2: Crear DTOs de Response

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

**✅ Checklist Fase 4:**
- [ ] DTOs de Request creados
- [ ] DTOs de Response creados
- [ ] Validaciones agregadas
- [ ] Compila sin errores

---

## 🌐 Fase 5: Controladores (Controller Layer)

### ¿Por qué al final?
Los controladores orquestan todo. Necesitas servicios y DTOs listos.

### Paso 5.1: Crear ExchangeRateController

**Archivo:** `src/main/java/com/example/demo/controllers/ExchangeRateController.java`

**Empieza con un endpoint simple:**

```java
package com.example.demo.controllers;

import com.example.demo.dto.ExchangeRateRequest;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.service.ExchangeRateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {
    
    @Autowired
    private ExchangeRateService service;
    
    // 1. Endpoint simple: GET por ID
    @GetMapping("/{id}")
    public Mono<ExchangeRateResponse> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponse);
    }
    
    // 2. Endpoint simple: GET todos
    @GetMapping
    public Flux<ExchangeRateResponse> findAll() {
        return service.findAll()
                .map(this::toResponse);
    }
    
    // 3. Endpoint con validación: POST crear
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ExchangeRateResponse> create(
            @Valid @RequestBody ExchangeRateRequest request) {
        
        ExchangeRate entity = ExchangeRate.builder()
                .monedaOrigen(request.getMonedaOrigen())
                .monedaDestino(request.getMonedaDestino())
                .tipoCambio(request.getTipoCambio())
                .build();
        
        return service.create(entity)
                .map(this::toResponse);
    }
    
    // Método helper: convertir Entity a DTO
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

**¿Qué aprendes aquí?**
- `@RestController`: Marca como controlador REST
- `@RequestMapping`: Ruta base del controlador
- `@GetMapping`, `@PostMapping`: Métodos HTTP
- `@Valid`: Activa validaciones de DTOs
- `Mono<T>` y `Flux<T>`: Retornos reactivos
- `map()`: Transforma Entity a DTO

**✅ Checklist Fase 5:**
- [ ] Controlador básico creado
- [ ] Endpoints funcionan
- [ ] Validaciones activas
- [ ] Compila sin errores

**🧪 Prueba rápida:**
1. Inicia la app
2. Prueba con Postman o cURL
3. Verifica que los endpoints responden

---

## 🔒 Fase 6: Seguridad (Security Layer)

### ¿Por qué al final?
La seguridad envuelve todo. Necesitas que todo funcione primero.

### Paso 6.1: Crear JwtUtil

**Archivo:** `src/main/java/com/example/demo/security/JwtUtil.java`

```java
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### Paso 6.2: Crear SecurityConfig

**Archivo:** `src/main/java/com/example/demo/security/SecurityConfig.java`

```java
package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .build();
    }
    
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN", "USER")
                .build();
        
        return new MapReactiveUserDetailsService(admin);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**✅ Checklist Fase 6:**
- [ ] JWT configurado
- [ ] SecurityConfig creado
- [ ] Endpoints protegidos
- [ ] Login funciona
- [ ] Compila sin errores

---

## 📚 Orden de Aprendizaje Recomendado

### Semana 1: Fundamentos
1. **Día 1-2:** Entidades y Repositorios
   - Aprende sobre R2DBC
   - Practica con Mono/Flux básicos
   
2. **Día 3-4:** Servicios
   - Aprende operadores reactivos (map, flatMap)
   - Practica encadenar operaciones

3. **Día 5:** DTOs y Validaciones
   - Aprende Jakarta Validation
   - Practica crear DTOs

### Semana 2: API y Seguridad
4. **Día 1-2:** Controladores
   - Aprende WebFlux controllers
   - Practica endpoints REST

5. **Día 3-4:** Seguridad JWT
   - Aprende Spring Security WebFlux
   - Practica autenticación

6. **Día 5:** Testing
   - Aprende tests reactivos
   - Practica con StepVerifier

---

## 🎯 Tips de Aprendizaje

### 1. **Empieza Simple**
- Crea un endpoint que solo retorne "Hello World"
- Luego agrega complejidad paso a paso

### 2. **Prueba Cada Capa**
- No esperes a terminar todo para probar
- Prueba repositorios → servicios → controladores

### 3. **Lee los Errores**
- Los errores de compilación te enseñan mucho
- Stack traces te muestran dónde está el problema

### 4. **Usa Tests**
- Crea tests simples para cada componente
- Te ayuda a entender cómo funciona

### 5. **Experimenta**
- Cambia valores y ve qué pasa
- Prueba diferentes operadores reactivos

---

## 🐛 Errores Comunes y Soluciones

### Error: "Cannot resolve symbol ReactiveCrudRepository"
**Solución:** Agrega dependencia `spring-boot-starter-data-r2dbc` en pom.xml

### Error: "Mono cannot be resolved"
**Solución:** Agrega import: `import reactor.core.publisher.Mono;`

### Error: "Table not found"
**Solución:** Verifica que `schema.sql` esté en `src/main/resources/`

### Error: "Method must return Mono or Flux"
**Solución:** En WebFlux, todos los métodos deben retornar Mono/Flux

---

## ✅ Checklist Final del Proyecto

- [ ] Entidades creadas
- [ ] Repositorios creados
- [ ] Servicios creados
- [ ] DTOs creados
- [ ] Controladores creados
- [ ] Seguridad configurada
- [ ] Tests básicos creados
- [ ] Documentación (README)

---

## 🚀 Siguiente Paso Después de Esta Guía

1. **Agrega más funcionalidades:**
   - Actualizar tipo de cambio
   - Eliminar tipo de cambio
   - Aplicar conversión con auditoría

2. **Mejora el código:**
   - Manejo de excepciones
   - Validaciones más complejas
   - Logging

3. **Aprende más:**
   - Tests unitarios reactivos
   - Integración continua
   - Documentación con Swagger

¡Buena suerte construyendo tu proyecto! 🎉


