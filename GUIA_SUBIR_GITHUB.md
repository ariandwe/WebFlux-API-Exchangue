# 🚀 Guía: Subir Proyecto a GitHub (Ubuntu)

## 📋 Prerrequisitos

1. Tener una cuenta en GitHub (https://github.com)
2. Tener Git instalado en Ubuntu
3. Estar en el directorio del proyecto

---

## 🔍 Paso 1: Verificar Git

Primero, verifica si Git está instalado:

```bash
git --version
```

Si NO está instalado, instálalo:

```bash
sudo apt update
sudo apt install git
```

---

## ⚙️ Paso 2: Configurar Git (Solo la primera vez)

Si es la primera vez que usas Git en esta máquina, configura tu nombre y email:

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu-email@example.com"
```

**Ejemplo:**
```bash
git config --global user.name "Juan Pérez"
git config --global user.email "juan.perez@email.com"
```

Verifica la configuración:

```bash
git config --list
```

---

## 📁 Paso 3: Navegar al Directorio del Proyecto

Navega al directorio del proyecto:

```bash
cd /home/aaranda/Música/demo
```

O si estás en otro lugar:

```bash
cd ~/Música/demo
```

---

## 🗂️ Paso 4: Crear .gitignore (Importante)

Antes de hacer commit, crea un archivo `.gitignore` para excluir archivos innecesarios:

```bash
nano .gitignore
```

O usa tu editor favorito. Agrega este contenido:

```gitignore
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
*.swp
*.swo
*~

# Sistema operativo
.DS_Store
Thumbs.db

# Logs
*.log

# H2 Database files
*.db
*.mv.db
*.trace.db

# Spring Boot
application-local.properties
application-local.yml
```

Guarda el archivo (Ctrl+O, Enter, Ctrl+X en nano).

---

## 🎯 Paso 5: Inicializar Git

Inicializa Git en el proyecto:

```bash
git init
```

Esto crea un repositorio Git local en tu proyecto.

---

## ➕ Paso 6: Agregar Archivos al Staging Area

Agrega todos los archivos al staging area:

```bash
git add .
```

O si quieres ver qué archivos se agregaron:

```bash
git status
```

Si quieres agregar archivos específicos:

```bash
git add src/
git add pom.xml
git add README.md
```

---

## 💾 Paso 7: Hacer el Primer Commit

Crea tu primer commit:

```bash
git commit -m "Initial commit: Exchange Rate Service con WebFlux"
```

O un mensaje más descriptivo:

```bash
git commit -m "Initial commit: Sistema de tipos de cambio con Spring Boot WebFlux, R2DBC, H2 y JWT"
```

---

## 🔗 Paso 8: Crear Repositorio en GitHub

1. Ve a https://github.com
2. Click en el botón **"+"** (arriba a la derecha)
3. Click en **"New repository"**
4. Completa:
   - **Repository name:** `exchange-rate-service` (o el nombre que prefieras)
   - **Description:** "Sistema de tipos de cambio con Spring Boot WebFlux"
   - **Visibility:** Public o Private (elige)
   - **NO marques** "Initialize this repository with a README"
   - **NO agregues** .gitignore o license (ya lo tenemos)
5. Click en **"Create repository"**

---

## 🌐 Paso 9: Conectar con GitHub

GitHub te mostrará comandos. Elige la opción **"...or push an existing repository from the command line"**.

Ejecuta estos comandos (reemplaza `TU_USUARIO` con tu usuario de GitHub):

```bash
git remote add origin https://github.com/TU_USUARIO/exchange-rate-service.git
git branch -M main
git push -u origin main
```

**Ejemplo:**
```bash
git remote add origin https://github.com/juanperez/exchange-rate-service.git
git branch -M main
git push -u origin main
```

---

## 🔐 Paso 10: Autenticación (Si se requiere)

Si GitHub te pide autenticación, tienes varias opciones:

### Opción A: Personal Access Token (Recomendado)

1. Ve a GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click en **"Generate new token (classic)"**
3. Dale un nombre (ej: "Ubuntu PC")
4. Selecciona el scope `repo` (full control)
5. Click en **"Generate token"**
6. **Copia el token** (solo se muestra una vez)

Cuando Git te pida usuario y contraseña:
- **Username:** Tu usuario de GitHub
- **Password:** El token que copiaste (NO tu contraseña de GitHub)

### Opción B: SSH (Más seguro, para uso frecuente)

Si prefieres usar SSH (recomendado para uso frecuente):

1. Genera una clave SSH (si no tienes una):

```bash
ssh-keygen -t ed25519 -C "tu-email@example.com"
```

Presiona Enter para usar la ubicación predeterminada.

2. Inicia el agente SSH:

```bash
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
```

3. Copia la clave pública:

```bash
cat ~/.ssh/id_ed25519.pub
```

4. En GitHub → Settings → SSH and GPG keys → New SSH key
   - Título: "Ubuntu PC"
   - Key: Pega la clave que copiaste
   - Click en "Add SSH key"

5. Cambia la URL del remote a SSH:

```bash
git remote set-url origin git@github.com:TU_USUARIO/exchange-rate-service.git
```

6. Prueba la conexión:

```bash
ssh -T git@github.com
```

Deberías ver: "Hi TU_USUARIO! You've successfully authenticated..."

7. Haz push nuevamente:

```bash
git push -u origin main
```

---

## ✅ Paso 11: Verificar

Ve a tu repositorio en GitHub:

```
https://github.com/TU_USUARIO/exchange-rate-service
```

Deberías ver todos tus archivos allí.

---

## 🔄 Comandos Útiles para el Futuro

### Ver el estado del repositorio:

```bash
git status
```

### Agregar cambios:

```bash
git add .
```

### Hacer commit:

```bash
git commit -m "Descripción de los cambios"
```

### Subir cambios a GitHub:

```bash
git push
```

### Ver el historial de commits:

```bash
git log
```

### Ver qué cambios hiciste:

```bash
git diff
```

### Ver branches:

```bash
git branch
```

---

## 🐛 Solución de Problemas

### Error: "fatal: remote origin already exists"

Si ya existe el remote, actualízalo:

```bash
git remote set-url origin https://github.com/TU_USUARIO/exchange-rate-service.git
```

### Error: "failed to push some refs"

Si alguien más hizo cambios o GitHub creó archivos:

```bash
git pull origin main --allow-unrelated-histories
git push -u origin main
```

### Error: "Permission denied (publickey)"

Si estás usando SSH y tienes este error:

1. Verifica que agregaste la clave SSH en GitHub
2. Verifica que el agente SSH tiene la clave:

```bash
ssh-add -l
```

3. Si no aparece, agrégalo:

```bash
ssh-add ~/.ssh/id_ed25519
```

### Error: "Authentication failed"

Si usas Personal Access Token:

1. Verifica que el token tenga el scope `repo`
2. Usa el token como contraseña, NO tu contraseña de GitHub
3. Si expiró, genera uno nuevo

---

## 📚 Archivos que NO deberías subir

El `.gitignore` que creamos debería excluir estos archivos automáticamente:

- `target/` (compilados de Maven)
- `.idea/`, `.vscode/` (configuración del IDE)
- `*.log` (archivos de log)
- `*.db` (archivos de base de datos H2)

Para verificar qué se va a subir:

```bash
git status
```

Si ves archivos que NO quieres subir, agrégalos al `.gitignore`.

---

## 🎯 Checklist Rápido

- [ ] Git instalado y configurado
- [ ] Navegar al directorio del proyecto
- [ ] Crear `.gitignore`
- [ ] `git init`
- [ ] `git add .`
- [ ] `git commit -m "..."` 
- [ ] Crear repositorio en GitHub
- [ ] `git remote add origin ...`
- [ ] `git push -u origin main`
- [ ] Verificar en GitHub

---

## 🚀 Comandos Rápidos (Copy-Paste)

```bash
# 1. Verificar Git
git --version

# 2. Configurar (solo primera vez)
git config --global user.name "Tu Nombre"
git config --global user.email "tu-email@example.com"

# 3. Ir al proyecto
cd /home/aaranda/Música/demo

# 4. Inicializar Git
git init

# 5. Agregar archivos
git add .

# 6. Commit
git commit -m "Initial commit: Exchange Rate Service con WebFlux"

# 7. Conectar con GitHub (reemplaza TU_USUARIO)
git remote add origin https://github.com/TU_USUARIO/exchange-rate-service.git

# 8. Cambiar branch a main
git branch -M main

# 9. Subir a GitHub
git push -u origin main
```

---

¡Listo! Tu proyecto debería estar en GitHub ahora. 🎉

