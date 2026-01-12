# 🚀 Guía Completa de Postman - Paso a Paso

## 📥 Paso 1: Importar la Colección

1. Abre Postman
2. Click en **"Import"** (botón arriba a la izquierda)
3. Selecciona **"File"** 
4. Busca y selecciona el archivo: `postman_collection.json`
5. Click en **"Import"**

✅ Deberías ver la colección "Exchange Rate Service - Banco Pichincha" en el panel izquierdo

---

## ⚙️ Paso 2: Crear el Entorno (Variables)

### Opción A: Crear Entorno Manualmente

1. Click en el **⚙️ (engranaje)** arriba a la derecha → **"Manage Environments"**
2. Click en **"Add"** (o el botón "+")
3. Configura así:

   **Environment Name:** `Exchange Rate Service`
   
   **Variables:**
   - `baseUrl` 
     - Initial Value: `http://localhost:8080`
     - Current Value: `http://localhost:8080`
   
   - `token`
     - Initial Value: (deja vacío)
     - Current Value: (deja vacío)
   
   - `username`
     - Initial Value: (deja vacío)
     - Current Value: (deja vacío)

4. Click en **"Save"**
5. **IMPORTANTE:** Selecciona el entorno en el selector de arriba a la derecha (debe decir "Exchange Rate Service")

### Opción B: Importar Entorno (Más Fácil)

Si prefieres, puedo crear un archivo de entorno para importar directamente.

---

## 🧪 Paso 3: Probar el Flujo Completo

### 3.1 Login (Obtener Token)

1. Expande la carpeta **"Auth"**
2. Selecciona **"Login - Admin"**
3. Verifica que el entorno esté seleccionado arriba a la derecha
4. Click en **"Send"**

**✅ Resultado esperado:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

**🎯 El token se guarda automáticamente** gracias al script en el request.

### 3.2 Crear un Tipo de Cambio

1. Expande **"Exchange Rate"**
2. Selecciona **"POST Create Exchange Rate"**
3. Verifica que el body tenga:
```json
{
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "tipoCambio": 0.27
}
```
4. Click en **"Send"**

**✅ Resultado esperado:**
```json
{
  "id": 1,
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "tipoCambio": 0.27,
  "fechaActualizacion": "2025-12-08T21:30:00"
}
```

### 3.3 Buscar Tipo de Cambio

1. Selecciona **"GET Get Exchange Rate by Monedas"**
2. Verifica que tenga los parámetros:
   - `origen`: PEN
   - `destino`: USD
3. Click en **"Send"**

### 3.4 Aplicar Conversión (Lo más importante)

1. Expande **"Exchange"**
2. Selecciona **"POST Apply Exchange"**
3. Verifica el body:
```json
{
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "monto": 100.00
}
```
4. Click en **"Send"**

**✅ Resultado esperado:**
```json
{
  "monedaOrigen": "PEN",
  "monedaDestino": "USD",
  "montoInicial": 100.00,
  "montoConvertido": 27.00,
  "tipoCambioAplicado": 0.27,
  "fecha": "2025-12-08T21:30:00"
}
```

---

## 🔍 Verificar Variables

Para verificar que las variables están funcionando:

1. Click en el **👁️ (ojo)** arriba a la derecha (al lado del selector de entorno)
2. Deberías ver:
   - `baseUrl`: `http://localhost:8080`
   - `token`: `eyJhbGciOiJIUzUxMiJ9...` (después del login)
   - `username`: `admin` (después del login)

---

## 🐛 Solución de Problemas

### Error: "baseUrl is not defined"
- **Solución:** Asegúrate de haber seleccionado el entorno arriba a la derecha

### Error: "401 Unauthorized"
- **Solución:** 
  1. Vuelve a hacer login
  2. Verifica que el token se guardó (click en el ojo 👁️)
  3. Si no se guardó, copia el token manualmente y pégalo en la variable `token`

### Error: "Could not get any response"
- **Solución:** Verifica que la aplicación esté corriendo en `http://localhost:8080`

---

## 📸 Capturas de Referencia

### Dónde está el selector de entorno:
```
[Postman] → [Selector arriba a la derecha] → "Exchange Rate Service"
```

### Dónde ver las variables:
```
[👁️ Eye icon] → Muestra todas las variables del entorno actual
```

---

## 🎯 Orden Recomendado de Pruebas

1. ✅ **Login - Admin** (obtener token)
2. ✅ **POST Create Exchange Rate** (crear tipo de cambio)
3. ✅ **GET Get Exchange Rate by Monedas** (buscar)
4. ✅ **POST Apply Exchange** (aplicar conversión - esto guarda auditoría)
5. ✅ **GET Get All Exchange Rates** (listar todos)
6. ✅ **PUT Update Exchange Rate** (actualizar)
7. ✅ **DELETE Delete Exchange Rate** (eliminar)

