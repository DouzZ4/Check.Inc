#!/bin/bash
set -e

# Configurar variables de entorno para MySQL desde Railway
# Railway proporciona estas variables automáticamente
MYSQL_HOST=${MYSQLHOST:-${MYSQL_HOST:-mysql.railway.internal}}
MYSQL_PORT=${MYSQLPORT:-${MYSQL_PORT:-3306}}
MYSQL_DATABASE=${MYSQLDATABASE:-${MYSQL_DATABASE:-railway}}
MYSQL_USER=${MYSQLUSER:-${MYSQL_USER:-root}}
MYSQL_PASSWORD=${MYSQLPASSWORD:-${MYSQL_PASSWORD:-""}}

# Construir la URL de conexión JDBC con credenciales
# Si hay contraseña, incluirla en la URL; si no, usar solo usuario
if [ -n "$MYSQL_PASSWORD" ] && [ "$MYSQL_PASSWORD" != "" ]; then
    JDBC_URL="jdbc:mysql://${MYSQL_USER}:${MYSQL_PASSWORD}@${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
else
    JDBC_URL="jdbc:mysql://${MYSQL_USER}@${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
fi

echo "=========================================="
echo "Configurando conexión a MySQL"
echo "=========================================="
echo "Host: ${MYSQL_HOST}"
echo "Port: ${MYSQL_PORT}"
echo "Database: ${MYSQL_DATABASE}"
echo "User: ${MYSQL_USER}"
echo "Password: [${#MYSQL_PASSWORD} caracteres]"
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
exec java -jar /opt/payara/payara-micro.jar \
  --addlibs /opt/payara/mysql-connector.jar \
  --postbootcommandfile /tmp/post-boot-commands.txt \
  --deploy /opt/payara/deployments/ROOT.war \
  --port 8080

