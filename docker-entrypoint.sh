#!/bin/bash
set -e

# Configurar variables de entorno para MySQL desde Railway
# Railway proporciona estas variables automáticamente
MYSQL_HOST=${MYSQLHOST:-${MYSQL_HOST:-mysql.railway.internal}}
MYSQL_PORT=${MYSQLPORT:-${MYSQL_PORT:-3306}}
MYSQL_DATABASE=${MYSQLDATABASE:-${MYSQL_DATABASE:-railway}}
MYSQL_USER=${MYSQLUSER:-${MYSQL_USER:-root}}
MYSQL_PASSWORD=${MYSQLPASSWORD:-${MYSQL_PASSWORD:-""}}

# Construir la URL de conexión JDBC
JDBC_URL="jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"

echo "=========================================="
echo "Configurando conexión a MySQL"
echo "=========================================="
echo "Host: ${MYSQL_HOST}"
echo "Port: ${MYSQL_PORT}"
echo "Database: ${MYSQL_DATABASE}"
echo "User: ${MYSQL_USER}"
echo "=========================================="

# Crear archivo de configuración para Payara Micro
cat > /tmp/post-boot-commands.txt <<EOF
# Configurar el pool de conexiones MySQL
create-jdbc-connection-pool \
  --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
  --restype javax.sql.DataSource \
  --property serverName=${MYSQL_HOST}:portNumber=${MYSQL_PORT}:databaseName=${MYSQL_DATABASE}:User=${MYSQL_USER}:Password=${MYSQL_PASSWORD}:URL="${JDBC_URL}" \
  mysql_checks_pool

# Crear el recurso JDBC
create-jdbc-resource \
  --connectionpoolid mysql_checks_pool \
  java:app/jdbc/checks

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

