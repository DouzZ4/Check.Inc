# Guía de Despliegue en Railway

Esta guía te ayudará a desplegar tu aplicación Check.Inc en Railway.

## Prerrequisitos

1. Una cuenta en [Railway](https://railway.app)
2. Un servicio MySQL ya creado en Railway (ya lo tienes)
3. Git configurado en tu máquina local

## Paso 1: Preparar el Repositorio

Asegúrate de que todos los archivos necesarios estén en tu repositorio:

- `Dockerfile`
- `docker-entrypoint.sh`
- `.dockerignore`
- `pom.xml`
- `src/` (código fuente)

## Paso 2: Conectar tu Repositorio a Railway

1. Inicia sesión en [Railway](https://railway.app)
2. Haz clic en **"New Project"**
3. Selecciona **"Deploy from GitHub repo"** (o el servicio Git que uses)
4. Conecta tu repositorio y selecciona el proyecto `Check.Inc`

## Paso 3: Configurar el Servicio de Aplicación

Railway detectará automáticamente el `Dockerfile` y comenzará a construir la imagen.

### Configurar Variables de Entorno

En la configuración de tu servicio de aplicación, ve a la pestaña **"Variables"** y agrega las siguientes variables de entorno:

**IMPORTANTE**: Railway proporciona automáticamente estas variables para servicios MySQL conectados, pero asegúrate de que estén disponibles:

```
MYSQLHOST=mysql.railway.internal
MYSQLPORT=3306
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=QdKWHIjuGCJNlAuWPKZDJTQoUsyVMEkj
```

**Nota**: Si Railway no proporciona automáticamente estas variables, puedes agregarlas manualmente usando los valores de tu servicio MySQL.

### Variables Opcionales

Si tu aplicación usa SendGrid u otros servicios externos, agrega también:

```
SENDGRID_API_KEY=tu_api_key_aqui
```

## Paso 4: Conectar el Servicio MySQL

1. En tu proyecto de Railway, asegúrate de que el servicio MySQL esté en el mismo proyecto
2. Railway automáticamente creará una red interna entre los servicios
3. El servicio de aplicación usará `mysql.railway.internal` como hostname para conectarse a MySQL

## Paso 5: Configurar el Puerto

Railway asignará automáticamente un puerto, pero la aplicación está configurada para usar el puerto 8080 internamente. Railway redirigirá el tráfico externo a este puerto automáticamente.

## Paso 6: Desplegar

1. Railway comenzará a construir la imagen Docker automáticamente
2. Una vez completada la construcción, la aplicación se desplegará automáticamente
3. Puedes ver los logs en tiempo real en la pestaña **"Deployments"**

## Paso 7: Verificar el Despliegue

1. Una vez desplegado, Railway te proporcionará una URL pública (algo como `tu-app.railway.app`)
2. Visita la URL para verificar que la aplicación esté funcionando
3. Revisa los logs si hay algún problema

## Solución de Problemas

### Error de Conexión a MySQL

Si ves errores de conexión a la base de datos:

1. Verifica que las variables de entorno estén configuradas correctamente
2. Asegúrate de que el servicio MySQL esté en el mismo proyecto
3. Verifica que el servicio MySQL esté ejecutándose
4. Revisa los logs del servicio MySQL

### Error en el Build

Si el build falla:

1. Revisa los logs de construcción en Railway
2. Verifica que el `Dockerfile` esté en la raíz del proyecto
3. Asegúrate de que `docker-entrypoint.sh` tenga permisos de ejecución (se configuran automáticamente en el Dockerfile)

### La Aplicación no Responde

1. Verifica que el puerto 8080 esté expuesto en el Dockerfile
2. Revisa los logs de la aplicación
3. Verifica que el WAR se haya construido correctamente

## Comandos Útiles

### Ver Logs en Tiempo Real

En Railway, ve a la pestaña **"Deployments"** y haz clic en el deployment activo para ver los logs.

### Reiniciar el Servicio

Puedes reiniciar el servicio desde el dashboard de Railway o haciendo un nuevo commit que fuerce un nuevo deployment.

## Notas Importantes

1. **Base de Datos**: Asegúrate de que tu base de datos MySQL tenga las tablas necesarias. Puedes ejecutar los scripts SQL desde `Db/checks.sql` si es necesario.

2. **Variables de Entorno**: Railway puede proporcionar automáticamente las variables de MySQL si los servicios están conectados. Si no, agrégalas manualmente.

3. **Puerto**: La aplicación usa el puerto 8080 internamente. Railway maneja el enrutamiento externo automáticamente.

4. **SSL**: La conexión a MySQL usa `useSSL=false` por defecto. Si necesitas SSL, modifica el `docker-entrypoint.sh`.

## Soporte

Si encuentras problemas, revisa:
- Los logs de Railway
- Los logs de la aplicación en la consola
- La documentación de Railway: https://docs.railway.app

