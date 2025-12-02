#!/bin/bash
set -e

# Configurar puerto dinámico (Render usa PORT, Railway usa 8080 por defecto)
APP_PORT=${PORT:-8080}

# Función para parsear DATABASE_URL (formato: mysql://user:password@host:port/database)
parse_database_url() {
    if [ -n "$DATABASE_URL" ]; then
        # Remover el prefijo mysql://
        URL=${DATABASE_URL#mysql://}
        
        # Extraer usuario y contraseña (si existen)
        if [[ $URL == *"@"* ]]; then
            CREDENTIALS=${URL%%@*}
            REST=${URL#*@}
            
            if [[ $CREDENTIALS == *":"* ]]; then
                MYSQL_USER=${CREDENTIALS%%:*}
                MYSQL_PASSWORD=${CREDENTIALS#*:}
            else
                MYSQL_USER=$CREDENTIALS
                MYSQL_PASSWORD=""
            fi
            
            # Extraer host, puerto y base de datos
            HOST_PORT_DB=${REST%%\?*}
            if [[ $HOST_PORT_DB == *"/"* ]]; then
                HOST_PORT=${HOST_PORT_DB%%/*}
                MYSQL_DATABASE=${HOST_PORT_DB#*/}
            else
                HOST_PORT=$HOST_PORT_DB
                MYSQL_DATABASE=""
            fi
            
            if [[ $HOST_PORT == *":"* ]]; then
                MYSQL_HOST=${HOST_PORT%%:*}
                MYSQL_PORT=${HOST_PORT#*:}
            else
                MYSQL_HOST=$HOST_PORT
                MYSQL_PORT=3306
            fi
        fi
    fi
}

# Intentar parsear DATABASE_URL primero (Render puede proporcionarla)
if [ -n "$DATABASE_URL" ]; then
    echo "=========================================="
    echo "Detectada DATABASE_URL, parseando..."
    echo "=========================================="
    parse_database_url
fi

# Configurar variables de entorno para MySQL
# Compatible con Railway (MYSQLHOST, MYSQLPORT, etc.) y Render (variables separadas o DATABASE_URL)
MYSQL_HOST=${MYSQLHOST:-${MYSQL_HOST:-${DATABASE_HOST:-""}}}
MYSQL_PORT=${MYSQLPORT:-${MYSQL_PORT:-${DATABASE_PORT:-3306}}}
MYSQL_DATABASE=${MYSQLDATABASE:-${MYSQL_DATABASE:-${DATABASE_NAME:-${DATABASE_DB:-""}}}}}
MYSQL_USER=${MYSQLUSER:-${MYSQL_USER:-${DATABASE_USER:-""}}}
MYSQL_PASSWORD=${MYSQLPASSWORD:-${MYSQL_PASSWORD:-${DATABASE_PASSWORD:-""}}}

# Validar que tenemos las variables necesarias
# Mostrar advertencia pero permitir continuar (útil para testing sin DB)
if [ -z "$MYSQL_HOST" ] || [ -z "$MYSQL_DATABASE" ] || [ -z "$MYSQL_USER" ]; then
    echo "=========================================="
    echo "⚠️  ADVERTENCIA: Variables de MySQL no configuradas"
    echo "=========================================="
    echo "La aplicación puede no funcionar correctamente sin la base de datos."
    echo "Configura las siguientes variables de entorno en Render:"
    echo "  - MYSQL_HOST (o DATABASE_URL)"
    echo "  - MYSQL_DATABASE"
    echo "  - MYSQL_USER"
    echo "  - MYSQL_PASSWORD (opcional)"
    echo ""
    echo "Continuando con valores por defecto (la app fallará al conectarse)..."
    echo "=========================================="
    # Usar valores por defecto para evitar errores de sintaxis
    MYSQL_HOST=${MYSQL_HOST:-"localhost"}
    MYSQL_DATABASE=${MYSQL_DATABASE:-"checks"}
    MYSQL_USER=${MYSQL_USER:-"root"}
fi

# Construir la URL de conexión JDBC con credenciales
# Si hay contraseña, incluirla en la URL; si no, usar solo usuario
if [ -n "$MYSQL_PASSWORD" ] && [ "$MYSQL_PASSWORD" != "" ]; then
    JDBC_URL="jdbc:mysql://${MYSQL_USER}:${MYSQL_PASSWORD}@${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
else
    JDBC_URL="jdbc:mysql://${MYSQL_USER}@${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
fi

echo "=========================================="
echo "Configurando aplicación Check.Inc"
echo "=========================================="
echo "Puerto de la aplicación: ${APP_PORT}"
echo ""
echo "Configuración de MySQL:"
echo "  Host: ${MYSQL_HOST}"
echo "  Port: ${MYSQL_PORT}"
echo "  Database: ${MYSQL_DATABASE}"
echo "  User: ${MYSQL_USER}"
echo "  Password: [${#MYSQL_PASSWORD} caracteres]"
echo "=========================================="

# Crear archivo de configuración para Payara Micro
# Usar la URL JDBC completa que incluye las credenciales
cat > /tmp/post-boot-commands.txt <<EOF
# Configurar el pool de conexiones MySQL usando URL completa
create-jdbc-connection-pool \
  --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
  --restype javax.sql.DataSource \
  --property URL="${JDBC_URL}" \
  mysql_checks_pool

# Crear el recurso JDBC
# Payara Micro no permite ':' en el nombre del recurso al crearlo con CLI
# Pero cuando la aplicación busca java:app/jdbc/checks, Payara lo resuelve
# automáticamente desde jdbc/checks en el contexto de la aplicación
create-jdbc-resource \
  --connectionpoolid mysql_checks_pool \
  jdbc/checks

# Verificar que el pool se creó correctamente
list-jdbc-connection-pools
list-jdbc-resources
EOF

# Ejecutar Payara Micro con el WAR y los comandos de configuración
# Usar puerto dinámico (Render proporciona PORT, fallback a 8080)
exec java -jar /opt/payara/payara-micro.jar \
  --addlibs /opt/payara/mysql-connector.jar \
  --postbootcommandfile /tmp/post-boot-commands.txt \
  --deploy /opt/payara/deployments/ROOT.war \
  --port ${APP_PORT}

