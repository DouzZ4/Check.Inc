# Etapa 1: Construcción del WAR
FROM maven:3.8.6-openjdk-8 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias (se cachean si no cambia el pom.xml)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir el WAR
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución con Payara Micro
FROM eclipse-temurin:8-jre

WORKDIR /app

# Variables de entorno para Payara Micro
ENV PAYARA_VERSION=5.2022.5
ENV PAYARA_HOME=/opt/payara
ENV DEPLOY_DIR=/opt/payara/deployments

# Instalar dependencias necesarias
RUN apt-get update && \
    apt-get install -y curl unzip && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean

# Descargar e instalar Payara Micro
RUN curl -L -o /tmp/payara-micro.jar \
    https://repo1.maven.org/maven2/fish/payara/extras/payara-micro/${PAYARA_VERSION}/payara-micro-${PAYARA_VERSION}.jar && \
    mkdir -p ${PAYARA_HOME} && \
    mv /tmp/payara-micro.jar ${PAYARA_HOME}/payara-micro.jar

# Copiar el WAR construido desde la etapa anterior
COPY --from=build /app/target/*.war ${DEPLOY_DIR}/ROOT.war

# Descargar el driver MySQL Connector/J
RUN curl -L -o ${PAYARA_HOME}/mysql-connector.jar \
    https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar

# Crear script de inicio que configura el datasource
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Crear script para healthcheck que usa el puerto dinámico
RUN echo '#!/bin/sh\nPORT=${PORT:-8080}\ncurl -f http://localhost:${PORT}/ || exit 1' > /healthcheck.sh && \
    chmod +x /healthcheck.sh

# Exponer el puerto (Render usa PORT dinámico, 8080 como fallback)
EXPOSE 8080

# Variables de entorno para MySQL (se configurarán desde Render/Railway)
# Render puede proporcionar DATABASE_URL o variables separadas
ENV MYSQL_HOST=""
ENV MYSQL_PORT=3306
ENV MYSQL_DATABASE=""
ENV MYSQL_USER=""
ENV MYSQL_PASSWORD=""
ENV DATABASE_URL=""
ENV PORT=8080

# Healthcheck (usa script que lee PORT dinámico)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD /healthcheck.sh

# Ejecutar Payara Micro
ENTRYPOINT ["/docker-entrypoint.sh"]

