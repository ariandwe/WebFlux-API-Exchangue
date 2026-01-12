# 🗄️ Guía para Ver la Base de Datos H2

## ⚠️ IMPORTANTE: Consola H2 no disponible con WebFlux

**La consola H2 tradicional NO funciona con Spring WebFlux** porque está diseñada para Spring MVC (servlet-based).

**✅ Solución:** Usamos endpoints REST para consultar los datos (más reactivo y alineado con la arquitectura).

---

## 🚀 Opción 1: Usar Endpoints REST (Recomendado)

### Paso 1: Obtener Token

Primero necesitas hacer login como admin:

```bash
# En Postman o cURL
POST http://localhost:8080/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

Copia el token del response.

### Paso 2: Consultar Tipos de Cambio

```bash
GET http://localhost:8080/db/exchange-rates
Headers: Authorization: Bearer <tu-token>
```

### Paso 3: Consultar Logs de Auditoría

```bash
GET http://localhost:8080/db/audit-logs
Headers: Authorization: Bearer <tu-token>
```

### En Postman:

1. **Login - Admin** → Obtener token
2. **GET /db/exchange-rates** → Ver tipos de cambio
3. **GET /db/audit-logs** → Ver logs de auditoría

---

## 🚀 Opción 2: Usar H2 Console Standalone (Alternativa)

Si realmente necesitas la consola H2, puedes descargar H2 standalone:

1. Descarga H2: https://www.h2database.com/html/download.html
2. Ejecuta: `java -jar h2-*.jar`
3. Conecta con:
   - **JDBC URL:** `jdbc:h2:mem:exchange_rate_db`
   - **Usuario:** `sa`
   - **Contraseña:** (vacío)

⚠️ **Nota:** Esto solo funciona si la base de datos está en memoria y la aplicación está corriendo.

---

## 📊 Ver los Datos (Usando REST API)

### Ver Tipos de Cambio

**Endpoint:** `GET /db/exchange-rates`

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/db/exchange-rates \
  -H "Authorization: Bearer <tu-token>"
```

**Response:**
```json
[
  {
    "id": 1,
    "monedaOrigen": "PEN",
    "monedaDestino": "USD",
    "tipoCambio": 0.27,
    "fechaActualizacion": "2025-12-09T10:30:00"
  }
]
```

### Ver Logs de Auditoría

**Endpoint:** `GET /db/audit-logs`

**Ejemplo con cURL:**
```bash
curl -X GET http://localhost:8080/db/audit-logs \
  -H "Authorization: Bearer <tu-token>"
```

**Response:**
```json
[
  {
    "id": 1,
    "usuario": "admin",
    "monedaOrigen": "PEN",
    "monedaDestino": "USD",
    "montoInicial": 100.00,
    "montoConvertido": 27.00,
    "tipoCambioAplicado": 0.27,
    "fecha": "2025-12-09T10:30:00"
  }
]
```

### Ver Estructura de las Tablas

Las tablas se crean automáticamente al iniciar la aplicación. Estructura:

**exchange_rates:**
- `id` (BIGINT, PK)
- `moneda_origen` (VARCHAR)
- `moneda_destino` (VARCHAR)
- `tipo_cambio` (DECIMAL)
- `fecha_actualizacion` (TIMESTAMP)

**audit_logs:**
- `id` (BIGINT, PK)
- `usuario` (VARCHAR)
- `moneda_origen` (VARCHAR)
- `moneda_destino` (VARCHAR)
- `monto_inicial` (DECIMAL)
- `monto_convertido` (DECIMAL)
- `tipo_cambio_aplicado` (DECIMAL)
- `fecha` (TIMESTAMP)

---

## 🔍 Consultas Útiles (Filtrar en el Cliente)

Como estamos usando REST API, puedes filtrar los resultados en tu cliente (Postman, navegador, etc.) o agregar endpoints adicionales si lo necesitas.

### Ver todos los tipos de cambio
```bash
GET /db/exchange-rates
```

### Ver todos los logs de auditoría
```bash
GET /db/audit-logs
```

### Filtrar en Postman

1. Obtén los datos con `GET /db/audit-logs`
2. En la respuesta JSON, puedes usar herramientas de Postman para filtrar
3. O simplemente revisa el JSON directamente

### Ejemplo de Response Completo

**GET /db/audit-logs** retorna:
```json
[
  {
    "id": 1,
    "usuario": "admin",
    "monedaOrigen": "PEN",
    "monedaDestino": "USD",
    "montoInicial": 100.00,
    "montoConvertido": 27.00,
    "tipoCambioAplicado": 0.27,
    "fecha": "2025-12-09T10:30:00"
  },
  {
    "id": 2,
    "usuario": "user",
    "monedaOrigen": "USD",
    "monedaDestino": "EUR",
    "montoInicial": 50.00,
    "montoConvertido": 45.00,
    "tipoCambioAplicado": 0.90,
    "fecha": "2025-12-09T11:00:00"
  }
]
```

---

## ⚠️ Notas Importantes

1. **Base de datos en memoria:** Los datos se pierden cuando reinicias la aplicación
2. **Solo lectura recomendada:** No modifiques datos directamente desde H2, usa la API
3. **Datos iniciales:** Las tablas están vacías al inicio, crea datos usando Postman primero

---

## 🎯 Flujo Recomendado

1. ✅ **Crear datos con Postman:**
   - **Login - Admin** → Obtener token
   - **POST Create Exchange Rate** → Crear tipo de cambio (PEN → USD = 0.27)
   - **POST Apply Exchange** → Aplicar conversión (100 PEN → 27 USD)

2. ✅ **Verificar los datos:**
   - **GET /db/exchange-rates** → Ver tipos de cambio creados
   - **GET /db/audit-logs** → Ver logs de auditoría

3. ✅ **Verificar que la auditoría se guardó:**
   - En la respuesta de `GET /db/audit-logs` deberías ver:
     - El usuario que hizo la conversión (`admin`)
     - El monto inicial (`100.00`) y convertido (`27.00`)
     - El tipo de cambio aplicado (`0.27`)
     - La fecha de la operación

---

## 🐛 Solución de Problemas

### Error: "401 Unauthorized" al consultar /db/*
- **Solución:** 
  1. Asegúrate de haber hecho login como `admin` (no `user`)
  2. Verifica que el token esté en el header: `Authorization: Bearer <token>`
  3. Solo usuarios con rol `ADMIN` pueden acceder a estos endpoints

### Error: "No se encontraron datos"
- **Solución:** 
  1. Primero crea datos usando Postman:
     - Crea un tipo de cambio
     - Aplica una conversión
  2. Luego consulta los endpoints `/db/*`

### No veo las tablas en H2 Console
- **Solución:** La consola H2 no funciona con WebFlux. Usa los endpoints REST:
  - `GET /db/exchange-rates`
  - `GET /db/audit-logs`

