# 📘 **Manual Técnico - Check.Inc**

## 1. Portada

*   **Nombre del Proyecto:** Check.Inc - Sistema de Gestión Médica para Diabetes
*   **Tipo de Documento:** Manual Técnico
*   **Desarrollador:** Equipo de Desarrollo Check.Inc
*   **Institución:** [Nombre de la Institución]
*   **Fecha:** Diciembre 2025
*   **Versión del Sistema:** 1.0-SNAPSHOT

---

## 2. Tabla de Contenido

1.  Portada
2.  Tabla de Contenido
3.  Introducción
4.  Objetivos
5.  Alcance del Sistema
6.  Descripción General del Sistema
7.  Requisitos del Sistema
8.  Arquitectura del Sistema
9.  Diseño de la Base de Datos
10. Descripción de Módulos
11. Instalación del Sistema
12. Configuración del Sistema
13. Seguridad del Sistema
14. Pruebas del Sistema
15. Mantenimiento del Sistema
16. Glosario
17. Conclusiones
18. Referencias

---

## 3. Introducción

**Check.Inc** es un sistema web desarrollado bajo la plataforma Java Enterprise Edition (Java EE) diseñado para facilitar la gestión y el monitoreo de pacientes con diabetes.

El sistema permite a los pacientes llevar un registro detallado de sus niveles de glucosa, medicamentos, citas médicas y anomalías de salud. A su vez, ofrece herramientas de análisis y reportes que ayudan a visualizar la evolución del tratamiento. Este manual técnico está dirigido a desarrolladores, administradores de sistemas y personal de TI encargado del despliegue, mantenimiento y evolución del software.

---

## 4. Objetivos

### 4.1 Objetivo General

Proveer una plataforma web robusta y segura que permita la gestión integral de la información médica de pacientes diabéticos, facilitando el autocontrol y el seguimiento de su condición de salud.

### 4.2 Objetivos Específicos

*   Implementar un registro histórico de niveles de glucosa con detección automática de valores críticos.
*   Gestionar el inventario y cronograma de medicamentos del paciente.
*   Administrar citas médicas y recordatorios.
*   Generar reportes estadísticos exportables (PDF, CSV) para análisis médico.
*   Notificar alertas de salud vía correo electrónico (SendGrid) y notificaciones del sistema.

---

## 5. Alcance del Sistema

**El sistema abarca:**
*   Gestión de usuarios y autenticación segura con roles diferenciados (Paciente, Administrador).
*   Operaciones CRUD completas para glucosa, medicamentos, citas y anomalías.
*   Importación de datos masivos mediante archivos CSV.
*   Visualización de datos mediante gráficos y tablas dinámicas.
*   Envío de correos electrónicos transaccionales para alertas.

**El sistema NO abarca:**
*   Diagnóstico médico automatizado (el sistema es una herramienta de apoyo, no un doctor).
*   Integración directa con dispositivos hardware de medición (glucómetros) vía Bluetooth/IoT en esta versión.
*   Chat en tiempo real con médicos.

---

## 6. Descripción General del Sistema

*   **Tipo de Sistema:** Aplicación Web Empresarial (Java EE).
*   **Área:** Salud y Bienestar (HealthTech).
*   **Usuarios:**
    *   **Paciente:** Usuario final que registra su información diaria.
    *   **Administrador:** Gestiona la plataforma, usuarios y configuraciones globales.

---

## 7. Requisitos del Sistema

### 7.1 Requisitos de Hardware (Servidor)

*   **Procesador:** Intel Core i5 / AMD Ryzen 5 o superior (2 vCPU mínimo recomendado).
*   **Memoria RAM:** Mínimo 4 GB (Recomendado 8 GB para GlassFish + MySQL).
*   **Almacenamiento:** 20 GB de espacio libre en disco.

### 7.2 Requisitos de Software

*   **Sistema Operativo:** Windows Server, Linux (Ubuntu/CentOS), o macOS.
*   **Lenguaje:** Java JDK 8 (Update 300+ recomendado).
*   **Servidor de Aplicaciones:** GlassFish 5.x o Payara Server.
*   **Base de Datos:** MySQL 8.0 o MariaDB 10.4+.
*   **Herramienta de Construcción:** Apache Maven 3.6+.
*   **Frameworks y Librerías:**
    *   Java Server Faces (JSF 2.2)
    *   PrimeFaces 13.0.0 (Componentes UI)
    *   JPA / EclipseLink (Persistencia)
    *   OmniFaces 3.14
    *   OkHttp (Cliente HTTP)

---

## 8. Arquitectura del Sistema

El sistema sigue el patrón de arquitectura **Modelo-Vista-Controlador (MVC)** adaptado a Java EE:

1.  **Vista (Presentación):** Archivos `.xhtml` que utilizan JSF y PrimeFaces para la interfaz de usuario.
2.  **Controlador (Lógica de Negocio):** 'Managed Beans' (`@Named`, `@ViewScoped`) que procesan los eventos de la vista.
3.  **Modelo (Persistencia):** Entidades JPA que mapean las tablas de la base de datos y Servicios/Facades que manejan la lógica de datos.

**Estructura de Paquetes:**
*   `com.mycompany.checkinc.controller`: Recibe peticiones de la vista.
*   `com.mycompany.checkinc.entities`: Clases POJO mapeadas a la BD.
*   `com.mycompany.checkinc.services`: Lógica de negocio y acceso a datos.

---

## 9. Diseño de la Base de Datos

El sistema utiliza una base de datos relacional (MySQL).

### 9.1 Modelo Entidad-Relación (Tablas Principales)

*   **`usuario`**: Almacena datos personales, credenciales y contacto de emergencia.
    *   *PK:* `idUsuario`
    *   *FK:* `idRol`
*   **`rol`**: Define los roles del sistema ('admin', 'paciente').
*   **`glucosa`**: Registros de mediciones de azúcar en sangre.
    *   *PK:* `idGlucosa`
    *   *FK:* `idUsuario`
*   **`medicamento`**: Inventario de medicinas, dosis y frecuencias.
    *   *FK:* `idUsuario`
*   **`cita`**: Agenda de citas médicas.
    *   *FK:* `idUsuario`
*   **`anomalia`**: Registro de eventos de salud inusuales.
*   **`alerta`**: Notificaciones generadas por el sistema.
*   **`nivelesglucosa`**: Configuración personalizada de rangos de alerta por usuario.

---

## 10. Descripción de Módulos

### 10.1 Módulos de Seguridad
Encargado de la autenticación y autorización.
*   **Login/Registro:** Validación de credenciales y creación de cuentas.
*   **Filtros de Sesión:** Protege las rutas `/views/*` para evitar accesos no autorizados.

### 10.2 Módulo de Gestión de Glucosa
Permite el control de los niveles de azúcar.
*   **Registro Diario:** Formulario para ingresar nivel (mg/dL), fecha, hora y momento del día.
*   **Historial:** Tabla con filtros de búsqueda y paginación.
*   **Importación:** Carga masiva de datos desde archivos CSV.

### 10.3 Módulo de Reportes
Generación de documentos para análisis externo.
*   **Exportación:** Genera archivos PDF (usando iText) y CSV (Apache Commons CSV).
*   **Gráficos:** Visualización de tendencias usando JFreeChart en reportes o PrimeFaces Charts en dashboard.

### 10.4 Módulo de Notificaciones
Sistema de alertas proactivo.
*   **Detección:** Al guardar una glucosa, el sistema verifica si está fuera de rango.
*   **Envío:** Si es crítico, se envía un correo a través de la API de **SendGrid** al contacto de emergencia y al paciente.

---

## 11. Instalación del Sistema

1.  **Clonar Repositorio:**
    ```bash
    git clone https://github.com/usuario/Check.Inc.git
    ```
2.  **Base de Datos:**
    *   Crear base de datos `checks` en MySQL.
    *   Importar script `Db/checks.sql`.
3.  **Configuración de Conexión:**
    *   Editar `src/main/webapp/WEB-INF/glassfish-resources.xml` con usuario/password de MySQL.
4.  **Compilación:**
    ```bash
    mvn clean package
    ```
5.  **Despliegue:**
    *   Subir el archivo `.war` generado en la carpeta `target/` al servidor GlassFish.

---

## 12. Configuración del Sistema

### 12.1 Variables y Credenciales
*   **Base de Datos:** Configurada en el Pool de Conexiones de GlassFish (`checksPool`).
*   **API Keys:** Configuración de servicios externos.
    *   Archivo: `src/main/resources/config.properties`
    *   Variable: `SENDGRID_API_KEY`

### 12.2 Rutas y Puertos
*   **Puerto Web:** 8080 (Por defecto en GlassFish).
*   **Puerto BD:** 3306 (MySQL).
*   **URL Base:** `http://localhost:8080/CheckInc-1.0-SNAPSHOT/`

---

## 13. Seguridad del Sistema

*   **Encriptación:** Las contraseñas de los usuarios son hasheadas utilizando el algoritmo **BCrypt** antes de guardarse en la base de datos.
*   **Sesiones:** Timeout configurado en `web.xml` (15 minutos de inactividad).
*   **Validación de Inputs:** Uso de validadores JSF y Bean Validation para prevenir inyección SQL y XSS.

---

## 14. Pruebas del Sistema

*   **Pruebas Unitarias:** Implementadas con **JUnit 5** y **Mockito** para validar la lógica de servicios y cálculos críticos.
*   **Pruebas de Integración:** Verificación de conexión con base de datos y envío de correos (SendGrid).
*   **Pruebas Manuales:** Validación de flujo de usuario (Registro -> Login -> CRUD).

---

## 15. Mantenimiento del Sistema

*   **Backups:** Se recomienda realizar copias de seguridad diarias de la base de datos MySQL (`mysqldump`).
*   **Monitoreo:** Revisar logs de GlassFish (`server.log`) para detectar errores de ejecución.
*   **Actualizaciones:** Mantener actualizadas las dependencias Maven para parches de seguridad.

---

## 16. Glosario

*   **JSF (JavaServer Faces):** Framework para construir interfaces web basadas en componentes.
*   **PrimeFaces:** Librería de componentes visuales enriquecidos para JSF.
*   **Hipoglucemia:** Nivel de azúcar en sangre peligrosamente bajo.
*   **Hiperglucemia:** Nivel de azúcar en sangre peligrosamente alto.
*   **JPA:** Java Persistence API, estándar para mapeo objeto-relacional.

---

## 17. Conclusiones

El sistema Check.Inc cumple con los estándares de desarrollo Java EE, ofreciendo una solución escalable y segura. Su arquitectura modular facilita el mantenimiento y la futura incorporación de nuevas funcionalidades, como la integración con dispositivos móviles o IA para predicciones de salud.

---

## 18. Referencias

1.  Documentación Oficial de Java EE 7.
2.  Documentación de PrimeFaces User Guide (v13).
3.  SendGrid API Documentation.
4.  Manual de Usuario de MySQL 8.
