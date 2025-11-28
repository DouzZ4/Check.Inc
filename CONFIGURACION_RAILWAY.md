# Configuración Rápida para Railway

## Variables de Entorno a Configurar en Railway

En el dashboard de Railway, ve a tu servicio de aplicación → **Variables** y agrega:

```
MYSQLHOST=mysql.railway.internal
MYSQLPORT=3306
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=QdKWHIjuGCJNlAuWPKZDJTQoUsyVMEkj
```

**Nota**: Si Railway detecta automáticamente tu servicio MySQL y crea estas variables, no necesitas agregarlas manualmente. Verifica en la sección de Variables si ya existen.

## Pasos en Railway

1. **Conectar Repositorio**: 
   - New Project → Deploy from GitHub repo
   - Selecciona tu repositorio `Check.Inc`

2. **Railway detectará automáticamente**:
   - El `Dockerfile` en la raíz
   - Comenzará a construir la imagen

3. **Conectar MySQL**:
   - Asegúrate de que tu servicio MySQL esté en el mismo proyecto
   - Railway creará automáticamente la red interna

4. **Verificar Variables**:
   - Ve a Variables y confirma que `MYSQLHOST`, `MYSQLPORT`, etc. estén configuradas

5. **Desplegar**:
   - Railway desplegará automáticamente después del build
   - Obtendrás una URL pública (ej: `tu-app.railway.app`)

## Verificar que Funciona

1. Visita la URL proporcionada por Railway
2. Revisa los logs en Railway para ver si hay errores
3. La aplicación debería conectarse automáticamente a MySQL

## Si hay Problemas

- **Error de conexión MySQL**: Verifica que las variables de entorno estén correctas
- **Build falla**: Revisa los logs de construcción
- **App no responde**: Verifica los logs de la aplicación

