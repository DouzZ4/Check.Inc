# Check.Inc - Sistema de Gestión Médica para Diabetes

Sistema web desarrollado en Java EE para la gestión y monitoreo de pacientes con diabetes. Permite registrar y hacer seguimiento de niveles de glucosa, medicamentos, citas médicas y anomalías, con funcionalidades de alertas automáticas y generación de reportes.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Uso](#-uso)
- [Documentación](#-documentación)
- [Desarrollo](#-desarrollo)

## ✨ Características

### Módulos Principales

- **Gestión de Usuarios**
  - Registro y autenticación de pacientes
  - Perfiles de usuario con información personal
  - Gestión de roles (Paciente/Administrador)
  - Configuración de contactos de emergencia

- **Monitoreo de Glucosa**
  - Registro de niveles de glucosa con fecha y hora
  - Historial completo con filtros por fecha/mes
  - Detección automática de niveles críticos
  - Configuración de rangos personalizados por usuario
  - Importación masiva desde archivos CSV

- **Gestión de Medicamentos**
  - Registro de medicamentos con dosis y frecuencia
  - Consulta y edición de medicamentos registrados

- **Registro de Anomalías**
  - Registro manual de anomalías con síntomas y observaciones
  - Creación automática cuando se detectan niveles críticos de glucosa
  - Historial completo de anomalías

- **Gestión de Citas Médicas**
  - Registro de citas con fecha, hora y motivo
  - Consulta y gestión de citas programadas

- **Sistema de Alertas y Notificaciones**
  - Alertas automáticas por correo electrónico (SendGrid)
  - Notificaciones cuando se detectan niveles críticos
  - Registro de intentos de envío y resultados
  - Panel de alertas para administradores

- **Reportes y Estadísticas**
  - Generación de reportes en PDF y CSV
  - Gráficos de tendencia de glucosa
  - Reportes agrupados por mes
  - Dashboard con estadísticas para administradores
  - Estadísticas de usuarios, glucosa y anomalías

## 🛠 Tecnologías Utilizadas

### Backend
- **Java EE 7** - Plataforma empresarial
- **JSF 2.2** - Framework para interfaces web
- **PrimeFaces 13.0.0** - Biblioteca de componentes UI
- **JPA (EclipseLink)** - Persistencia de datos
- **CDI** - Inyección de dependencias
- **Maven** - Gestión de dependencias y build

### Base de Datos
- **MySQL 8.0+** - Sistema de gestión de base de datos

### Servidor de Aplicaciones
- **GlassFish** - Servidor de aplicaciones Java EE

### Librerías Adicionales
- **OmniFaces 3.14.1** - Utilidades para JSF
- **Apache Commons CSV 1.9.0** - Procesamiento de archivos CSV
- **iTextPDF 5.5.13.3** - Generación de PDFs
- **JFreeChart 1.0.13** - Generación de gráficos
- **Jackson 2.15.2** - Procesamiento JSON
- **OkHttp 4.12.0** - Cliente HTTP para APIs
- **jBCrypt 0.4** - Hashing de contraseñas
- **JUnit 5** - Framework de pruebas

### Servicios Externos
- **SendGrid** - Servicio de envío de correos electrónicos

## 📦 Requisitos Previos

- **Java JDK 8** o superior
- **Maven 3.6+**
- **MySQL 8.0+** o **MariaDB 10.4+**
- **GlassFish 5+** o servidor de aplicaciones compatible con Java EE 7
- **Git** (para clonar el repositorio)

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd Check.Inc
```

### 2. Configurar la Base de Datos

1. Crear la base de datos MySQL:
```sql
CREATE DATABASE checks CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. Importar el esquema de la base de datos:
```bash
mysql -u root -p checks < Db/checks.sql
```

### 3. Compilar el Proyecto

```bash
mvn clean package
```

El archivo WAR se generará en `target/CheckInc-1.0-SNAPSHOT.war`

## ⚙️ Configuración

### 1. Configurar el DataSource en GlassFish

El proyecto incluye un archivo `glassfish-resources.xml` que debe ser desplegado en GlassFish. Asegúrate de actualizar las credenciales de la base de datos:

**Archivo:** `src/main/webapp/WEB-INF/glassfish-resources.xml`

```xml
<property name="user" value="tu_usuario"/>
<property name="password" value="tu_contraseña"/>
<property name="url" value="jdbc:mysql://tu_servidor:3306/checks?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC"/>
```

### 2. Configurar SendGrid API Key

1. Obtén tu API Key de SendGrid desde [sendgrid.com](https://sendgrid.com)

2. Crea o edita el archivo `config.properties` en la raíz del proyecto:
```properties
SENDGRID_API_KEY=tu_api_key_aqui
```

**⚠️ Importante:** Este archivo está en `.gitignore` para proteger tus credenciales. No lo subas al repositorio.

### 3. Desplegar en GlassFish

1. Inicia GlassFish
2. Accede a la consola administrativa (normalmente en `http://localhost:4848`)
3. Despliega el archivo WAR:
   - Ve a **Applications** → **Deploy**
   - Selecciona `target/CheckInc-1.0-SNAPSHOT.war`
   - O usa la línea de comandos:
   ```bash
   asadmin deploy target/CheckInc-1.0-SNAPSHOT.war
   ```

4. Verifica que el DataSource esté configurado correctamente:
   - Ve a **Resources** → **JDBC** → **JDBC Connection Pools**
   - Verifica que `checksPool` esté configurado y funcional

## 📁 Estructura del Proyecto

```
Check.Inc/
├── Db/                          # Scripts de base de datos
│   ├── checks.sql              # Esquema completo de la BD
│   └── mysql-connector-j-8.0.33.jar
├── docs/                        # Documentación del proyecto
│   ├── requirements_funcionales.md
│   ├── user-stories.md
│   ├── plan_de_pruebas.md
│   ├── casos_de_prueba.md
│   └── informe_aceptacion_usuario.md
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/checkinc/
│   │   │   ├── controller/     # Managed Beans (JSF)
│   │   │   ├── entities/       # Entidades JPA
│   │   │   ├── services/       # Facades y servicios
│   │   │   ├── security/       # Filtros de seguridad
│   │   │   └── util/           # Utilidades
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   └── persistence.xml
│   │   │   └── config.properties
│   │   └── webapp/
│   │       ├── views/          # Páginas XHTML
│   │       ├── includes/       # Componentes reutilizables
│   │       ├── resources/      # CSS, JS, imágenes
│   │       └── WEB-INF/
│   │           ├── web.xml
│   │           ├── faces-config.xml
│   │           └── glassfish-resources.xml
│   └── test/                   # Pruebas unitarias
├── target/                      # Archivos compilados (generado)
├── config.properties           # Configuración local (no versionado)
├── pom.xml                     # Configuración Maven
└── README.md                   # Este archivo
```

## 💻 Uso

### Acceso a la Aplicación

Una vez desplegada, accede a la aplicación en:
```
http://localhost:8080/CheckInc-1.0-SNAPSHOT/
```

### Roles de Usuario

- **Paciente**: Puede registrar y gestionar sus propios datos (glucosa, medicamentos, citas, anomalías)
- **Administrador**: Acceso completo, incluyendo gestión de usuarios y estadísticas del sistema

### Funcionalidades Principales

1. **Registro de Usuario**: Crea una cuenta nueva desde la página de registro
2. **Login**: Inicia sesión con tu correo y contraseña
3. **Dashboard**: Visualiza resumen de tus datos y estadísticas
4. **Registro de Glucosa**: Ingresa tus niveles de glucosa diarios
5. **Importar Datos**: Sube archivos CSV con lecturas de glucosa
6. **Generar Reportes**: Descarga reportes en PDF o CSV de tus registros
7. **Gestión de Medicamentos**: Registra y gestiona tus medicamentos
8. **Citas Médicas**: Programa y gestiona tus citas
9. **Alertas**: Revisa las alertas generadas por el sistema

## 📚 Documentación

La documentación completa del proyecto se encuentra en la carpeta `docs/`:

- **Requisitos Funcionales**: `docs/requirements_funcionales.md`
- **Historias de Usuario**: `docs/user-stories.md`
- **Plan de Pruebas**: `docs/plan_de_pruebas.md`
- **Casos de Prueba**: `docs/casos_de_prueba.md`
- **Informe de Aceptación**: `docs/informe_aceptacion_usuario.md`

## 🔧 Desarrollo

### Ejecutar Pruebas

```bash
mvn test
```

### Compilar sin Pruebas

```bash
mvn clean package -DskipTests
```

### Estructura de Entidades

Las principales entidades del sistema son:

- `Usuario` - Información de usuarios/pacientes
- `Glucosa` - Registros de niveles de glucosa
- `Medicamento` - Medicamentos registrados
- `Anomalia` - Anomalías detectadas o registradas
- `Cita` - Citas médicas programadas
- `Alerta` - Alertas y notificaciones
- `NivelesGlucosa` - Configuración de rangos personalizados
- `Rol` - Roles de usuario
- `Reporte` - Reportes generados
- `Recordatorio` - Recordatorios programados
- `Notificacion` - Notificaciones del sistema

### Variables de Entorno Importantes

- `SENDGRID_API_KEY`: Clave API de SendGrid para envío de correos
- Configuración de base de datos en `glassfish-resources.xml`

## 🔒 Seguridad

- Las contraseñas se almacenan usando **BCrypt** (hashing seguro)
- Las sesiones tienen timeout configurado (15 minutos)
- Cookies configuradas con flags `http-only` y `secure`
- Filtro de seguridad implementado para proteger rutas

## ⚠️ Notas Importantes

1. **Credenciales**: Nunca subas archivos con credenciales reales al repositorio. El archivo `config.properties` está en `.gitignore` por seguridad.

2. **Base de Datos**: Asegúrate de que el DataSource esté correctamente configurado en GlassFish antes de desplegar.

3. **SendGrid**: Para desarrollo, considera usar una cuenta de prueba o configurar un mock para evitar costos.

4. **Java Version**: El proyecto está configurado para Java 8. Asegúrate de usar una versión compatible.

## 📝 Licencia

Este proyecto es privado. Todos los derechos reservados.

## 👥 Contacto

Para más información sobre el proyecto, consulta la documentación en la carpeta `docs/` o contacta al equipo de desarrollo.

---

**Check.Inc** - Sistema de Gestión Médica para Diabetes  
Versión: 1.0-SNAPSHOT


