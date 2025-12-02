# Guía de Despliegue en Render

Esta guía te ayudará a desplegar tu aplicación Check.Inc en Render, adaptando la configuración actual de Railway.

## ⚠️ Consideraciones Importantes para Render

### 1. Variables de Entorno de MySQL

**Diferencia clave**: Render proporciona las variables de MySQL con nombres diferentes a Railway.

Render proporciona automáticamente estas variables cuando conectas una base de datos MySQL:
- `DATABASE_URL` - URL completa de conexión (formato: `mysql://user:password@host:port/database`)
- O variables individuales (dependiendo de cómo configures la base de datos)

**Necesitas adaptar `docker-entrypoint.sh`** para que funcione con Render. Render puede proporcionar:
- `DATABASE_URL` (URL completa)
- O variables separadas como `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`

### 2. Puerto de la Aplicación

**CRÍTICO**: Render asigna dinámicamente un puerto a través de la variable de entorno `PORT`. Tu aplicación **DEBE** escuchar en el puerto especificado por `$PORT`, no en un puerto fijo como 8080.

**Necesitas modificar**:
- `docker-entrypoint.sh` para usar `$PORT` en lugar de `--port 8080`
- O configurar Payara Micro para leer el puerto desde la variable de entorno

### 3. Configuración de la Base de Datos

Render puede proporcionar la conexión de dos formas:

**Opción A**: URL completa en `DATABASE_URL`
```
mysql://usuario:contraseña@host:3306/nombre_base_datos
```

**Opción B**: Variables separadas (si configuras manualmente)
```
MYSQL_HOST=...
MYSQL_PORT=3306
MYSQL_DATABASE=...
MYSQL_USER=...
MYSQL_PASSWORD=...
```

### 4. Health Checks

Render requiere que tu aplicación responda correctamente a health checks. Tu Dockerfile ya tiene un HEALTHCHECK, pero asegúrate de que:
- La aplicación responda en la ruta raíz `/` o en un endpoint de health
- El healthcheck use el puerto correcto (no hardcodeado)

### 5. Build y Deploy

Render puede:
- Detectar automáticamente el Dockerfile
- O usar un comando de build personalizado
- Construir desde un repositorio Git (GitHub, GitLab, Bitbucket)

## 📋 Pasos para Desplegar en Render

### Paso 1: Preparar el Repositorio

Asegúrate de tener estos archivos en tu repositorio:
- ✅ `Dockerfile`
- ✅ `docker-entrypoint.sh`
- ✅ `.dockerignore`
- ✅ `pom.xml`
- ✅ `src/` (código fuente)

### Paso 2: Crear Servicio en Render

1. Inicia sesión en [Render](https://render.com)
2. Haz clic en **"New +"** → **"Web Service"**
3. Conecta tu repositorio (GitHub/GitLab/Bitbucket)
4. Selecciona el repositorio `Check.Inc`

### Paso 3: Configurar el Servicio

#### Configuración Básica:
- **Name**: `check-inc` (o el nombre que prefieras)
- **Environment**: `Docker`
- **Region**: Elige la región más cercana a tus usuarios
- **Branch**: `main` (o la rama que uses)

#### Build & Deploy:
- Render detectará automáticamente el `Dockerfile`
- **Build Command**: (dejar vacío, Render usará el Dockerfile)
- **Start Command**: (dejar vacío, el Dockerfile ya tiene ENTRYPOINT)

### Paso 4: Crear Base de Datos MySQL

1. En Render, ve a **"New +"** → **"PostgreSQL"** o **"MySQL"**
2. Si Render no ofrece MySQL directamente, puedes:
   - Usar un servicio externo de MySQL (como AWS RDS, PlanetScale, etc.)
   - O usar PostgreSQL y adaptar la aplicación (requiere más cambios)

**Nota**: Render actualmente ofrece PostgreSQL por defecto. Para MySQL, considera:
- Usar un servicio MySQL externo (AWS RDS, PlanetScale, Aiven, etc.)
- O adaptar la aplicación para usar PostgreSQL

### Paso 5: Configurar Variables de Entorno

En la sección **"Environment"** del servicio web, agrega:

#### Si usas MySQL externo:
```bash
MYSQL_HOST=tu-host-mysql.com
MYSQL_PORT=3306
MYSQL_DATABASE=nombre_base_datos
MYSQL_USER=usuario
MYSQL_PASSWORD=contraseña_segura
PORT=8080
```

#### Si Render proporciona DATABASE_URL:
Necesitarás parsear la URL en `docker-entrypoint.sh`:
```bash
DATABASE_URL=mysql://user:pass@host:3306/dbname
PORT=8080
```

### Paso 6: Conectar Base de Datos al Servicio

1. Si creaste la base de datos en Render:
   - Ve a la configuración de la base de datos
   - Copia las variables de entorno proporcionadas
   - Agréguelas al servicio web

2. Si usas MySQL externo:
   - Agrega manualmente las variables de entorno en el servicio web

### Paso 7: Configurar el Puerto Dinámico

**IMPORTANTE**: Render asigna un puerto dinámico. Necesitas modificar `docker-entrypoint.sh` para usar `$PORT`.

## 🔧 Modificaciones Necesarias

### Modificación 1: Actualizar `docker-entrypoint.sh` para Render

Necesitas modificar el script para:
1. Leer el puerto desde `$PORT` (Render lo proporciona)
2. Manejar `DATABASE_URL` si Render la proporciona
3. Mantener compatibilidad con variables separadas

### Modificación 2: Actualizar `Dockerfile` (opcional)

El Dockerfile puede necesitar ajustes menores, pero debería funcionar tal como está.

## 📝 Archivo `render.yaml` (Opcional)

Puedes crear un archivo `render.yaml` en la raíz para automatizar la configuración:

```yaml
services:
  - type: web
    name: check-inc
    env: docker
    dockerfilePath: ./Dockerfile
    envVars:
      - key: MYSQL_HOST
        sync: false
      - key: MYSQL_PORT
        value: 3306
      - key: MYSQL_DATABASE
        sync: false
      - key: MYSQL_USER
        sync: false
      - key: MYSQL_PASSWORD
        sync: false
      - key: PORT
        fromService:
          type: web
          name: check-inc
          property: port
```

## ⚠️ Problemas Comunes y Soluciones

### Problema 1: La aplicación no inicia

**Solución**:
- Verifica que el puerto esté configurado correctamente
- Revisa los logs en Render Dashboard
- Asegúrate de que `docker-entrypoint.sh` tenga permisos de ejecución

### Problema 2: Error de conexión a MySQL

**Solución**:
- Verifica que las variables de entorno estén correctamente configuradas
- Si usas MySQL externo, asegúrate de que el firewall permita conexiones desde Render
- Revisa que el host, puerto, usuario y contraseña sean correctos

### Problema 3: Build falla

**Solución**:
- Revisa los logs de build en Render
- Verifica que el Dockerfile esté en la raíz del proyecto
- Asegúrate de que todas las dependencias estén en `pom.xml`

### Problema 4: Health Check falla

**Solución**:
- Verifica que la aplicación responda en la ruta raíz `/`
- Asegúrate de que el healthcheck use el puerto correcto
- Revisa que Payara Micro esté escuchando en el puerto correcto

## 🔐 Seguridad

1. **Nunca** commitees contraseñas o API keys en el repositorio
2. Usa variables de entorno para todos los secretos
3. Render encripta las variables de entorno automáticamente
4. Considera usar secretos de Render para información sensible

## 📊 Monitoreo

Render proporciona:
- Logs en tiempo real
- Métricas de CPU y memoria
- Historial de deployments
- Alertas configurables

## 💰 Consideraciones de Costo

- Render ofrece un plan gratuito con limitaciones
- El plan gratuito puede tener:
  - Tiempo de inactividad (el servicio se "duerme" después de inactividad)
  - Límites de recursos
- Considera el plan pago para producción

## 🔄 Diferencias Clave: Railway vs Render

| Aspecto | Railway | Render |
|---------|---------|--------|
| Variables MySQL | `MYSQLHOST`, `MYSQLPORT`, etc. | `DATABASE_URL` o variables separadas |
| Puerto | 8080 fijo | `$PORT` dinámico |
| MySQL nativo | ✅ Sí | ❌ No (solo PostgreSQL) |
| Docker | ✅ Sí | ✅ Sí |
| Health Checks | Opcional | Recomendado |

## 📚 Recursos Adicionales

- [Documentación de Render](https://render.com/docs)
- [Render Docker Guide](https://render.com/docs/docker)
- [Render Environment Variables](https://render.com/docs/environment-variables)

## ✅ Checklist Pre-Deploy

Antes de desplegar, verifica:

- [ ] `docker-entrypoint.sh` está adaptado para usar `$PORT`
- [ ] Variables de entorno configuradas en Render
- [ ] Base de datos MySQL creada y accesible
- [ ] `Dockerfile` está en la raíz del proyecto
- [ ] `.dockerignore` está configurado correctamente
- [ ] Scripts SQL de inicialización listos (si es necesario)
- [ ] Health check configurado correctamente
- [ ] Logs de build sin errores

## 🚀 Siguiente Paso

Una vez que hayas completado estas consideraciones, puedes proceder con el despliegue. Render comenzará a construir la imagen Docker automáticamente cuando hagas push a tu repositorio.

