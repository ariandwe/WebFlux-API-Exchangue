# Exchange Rate Service - Banco Pichincha

## Descripción

Servicio backend desarrollado con **Spring Boot WebFlux** para la gestión de tipos de cambio y conversión de monedas. El proyecto implementa programación reactiva, autenticación JWT, y auditoría completa de operaciones.

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebFlux** (Programación Reactiva)
- **Spring Security** (Autenticación JWT)
- **R2DBC** (Reactive Database Connectivity)
- **H2 Database** (Base de datos en memoria)
- **JWT** (JSON Web Tokens)
- **Lombok** (Reducción de código boilerplate)
- **Maven** (Gestión de dependencias)

## Arquitectura

El proyecto sigue una arquitectura limpia/hexagonal con la siguiente estructura de paquetes:

```
com.example.demo
├── config          # Configuraciones (Security, Database, etc.)
├── controllers     # Controladores REST (WebFlux)
├── dto             # Data Transfer Objects
├── entity          # Entidades de dominio
├── repository      # Repositorios R2DBC
├── service         # Lógica de negocio (Reactiva)
├── security        # Configuración de seguridad JWT
└── exception       # Excepciones personalizadas y manejadores
```

### Flujo de Autenticación JWT

1. El cliente envía credenciales a `/auth/login`
2. El servicio valida las credenciales contra usuarios en memoria
3. Si son válidas, se genera un token JWT
4. El cliente incluye el token en el header `Authorization: Bearer <token>`
5. El filtro `JwtAuthenticationFilter` valida el token en cada request
6. Si es válido, se establece el contexto de seguridad

### Flujo de Aplicación de Tipo de Cambio

1. Cliente envía request a `/exchange/apply` con monedas y monto
2. El servicio busca el tipo de cambio en la base de datos
3. Se calcula el monto convertido (monto * tipoCambio)
4. Se registra la operación en la tabla de auditoría
5. Se retorna el resultado al cliente

## Requisitos Funcionales

### 1. CRUD de Tipos de Cambio
-  Crear tipo de cambio
-  Actualizar tipo de cambio
-  Buscar por moneda origen/destino
-  Listar todos los tipos de cambio
-  Obtener por ID
-  Eliminar tipo de cambio

### 2. Aplicar Tipo de Cambio
-  Aplicar tipo de cambio a un monto
-  Cálculo: `montoConvertido = montoInicial * tipoCambio`

### 3. Auditoría
-  Registro automático de cada operación de cambio
-  Campos registrados:
  - Usuario (desde JWT)
  - Fecha
  - Monto inicial
  - Monto convertido
  - Tipo de cambio aplicado
  - Monedas implicadas

### 4. Autenticación JWT
-  Endpoint `/auth/login` público
-  Usuarios en memoria (admin/user)
-  Resto de endpoints protegidos
-  Validación de token en cada request

## Endpoints

### Autenticación

#### POST /auth/login
Autentica un usuario y devuelve un token JWT.

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

### Tipos de Cambio

#### POST /exchange-rate
Crea un nuevo tipo de cambio.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "tipoCambio": 0.27
}
```

#### PUT /exchange-rate/{id}
Actualiza un tipo de cambio existente.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "tipoCambio": 0.28
}
```

#### GET /exchange-rate?origen=PEN&destino=USD
Busca un tipo de cambio por moneda origen y destino.

**Headers:** `Authorization: Bearer <token>`

#### GET /exchange-rate/all
Obtiene todos los tipos de cambio.

**Headers:** `Authorization: Bearer <token>`

#### GET /exchange-rate/{id}
Obtiene un tipo de cambio por ID.

**Headers:** `Authorization: Bearer <token>`

#### DELETE /exchange-rate/{id}
Elimina un tipo de cambio.

**Headers:** `Authorization: Bearer <token>`

### Aplicar Tipo de Cambio

#### POST /exchange/apply
Aplica un tipo de cambio a un monto y registra la operación en auditoría.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "monto": 100.00
}
```

**Response:**
```json
{
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "montoInicial": 100.00,
  "montoConvertido": 27.00,
  "tipoCambioAplicado": 0.27,
  "fecha": "2024-01-15T10:30:00"
}
```

## Usuarios en Memoria

| Usuario | Contraseña | Roles |
|---------|------------|-------|
| admin   | admin123   | ADMIN, USER |
| user    | user123    | USER |

## Configuración

### application.yml

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///exchange_rate_db
    username: sa
    password: 

server:
  port: 8080

jwt:
  secret: MySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS512Algorithm
  expiration: 86400000 # 24 horas
```

## Ejecución

### Prerrequisitos
- Java 17 o superior
- Maven 3.6+

### Compilar y Ejecutar

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

### Ejecutar Tests

```bash
mvn test
```

## Base de Datos

### Tablas

#### exchange_rates
- `id` (BIGINT, PK)
- `moneda_origen` (VARCHAR)
- `moneda_destino` (VARCHAR)
- `tipo_cambio` (DECIMAL)
- `fecha_actualizacion` (TIMESTAMP)
- UNIQUE(moneda_origen, moneda_destino)

#### audit_logs
- `id` (BIGINT, PK)
- `usuario` (VARCHAR)
- `moneda_origen` (VARCHAR)
- `moneda_destino` (VARCHAR)
- `monto_inicial` (DECIMAL)
- `monto_convertido` (DECIMAL)
- `tipo_cambio_aplicado` (DECIMAL)
- `fecha` (TIMESTAMP)

### H2 Console

Acceso a la consola H2: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:exchange_rate_db`
- Username: `sa`
- Password: (vacío)

## Tests

### Tests Unitarios

- `ExchangeRateServiceTest`: Tests para operaciones CRUD de tipos de cambio
- `ExchangeApplyServiceTest`: Tests para aplicación de tipos de cambio

### Ejecutar Tests

```bash
mvn test
```

## Postman Collection

Se incluye un archivo `postman_collection.json` con todos los endpoints configurados y listos para usar.

### Importar en Postman

1. Abrir Postman
2. Click en "Import"
3. Seleccionar el archivo `postman_collection.json`
4. Configurar la variable de entorno `token` con el token obtenido del login

## Características Técnicas

### Programación Reactiva
- Todos los métodos retornan `Mono` o `Flux`
- No hay bloqueos de threads
- Escalabilidad mejorada

### Seguridad
- Autenticación basada en JWT
- Filtro de seguridad reactivo
- Validación de tokens en cada request

### Validación
- Validación de DTOs con Jakarta Validation
- Manejo global de excepciones
- Mensajes de error descriptivos

### Auditoría
- Registro automático de operaciones
- Trazabilidad completa
- Información del usuario desde JWT

## Estructura del Proyecto

```
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/
│   │   │   ├── controllers/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── security/
│   │   │   └── exception/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── schema.sql
│   └── test/
│       └── java/com/example/demo/
│           └── service/
├── pom.xml
├── README.md
└── postman_collection.json
```

## Notas de Desarrollo

- El proyecto usa **Netty** como servidor por defecto (WebFlux)
- La base de datos H2 es en memoria, se reinicia en cada ejecución
- Los tokens JWT expiran en 24 horas
- La validación de tokens se realiza en cada request protegido

## Autor

Desarrollado como solución al reto técnico de Banco Pichincha.
Arian Aranda Egusquiza

