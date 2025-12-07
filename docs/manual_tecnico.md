# Manual Técnico - Check.Inc

## 1. Introducción
Este documento describe los aspectos técnicos, arquitectura y configuración del sistema **Check.Inc**, una aplicación Java EE para la gestión médica de diabetes.

## 2. Pila Tecnológica
*   **Lenguaje**: Java 8 (Java EE 7)
*   **Framework Web**: JavaServer Faces (JSF) 2.2
*   **Componentes UI**: PrimeFaces 13.0
*   **Persistencia**: JPA 2.1 (EclipseLink)
*   **Base de Datos**: MySQL 8.0
*   **Servidor de Aplicaciones**: GlassFish 5 / Payara 5
*   **Herramienta de Construcción**: Apache Maven 3.6+

## 3. Arquitectura del Sistema
El sistema sigue una arquitectura Modelo-Vista-Controlador (MVC) potenciada por las capas de Java EE:

1.  **Capa de Presentación (Vista)**: Archivos `.xhtml` con Facelets y PrimeFaces.
2.  **Capa de Control (Controlador)**: Managed Beans (`@Named`, `@ViewScoped`) que manejan la lógica de la UI.
3.  **Capa de Negocio (Servicios)**: EJBs (`@Stateless`) que contienen la lógica de negocio y transaccionalidad.
4.  **Capa de Datos (Modelo)**: Entidades JPA (`@Entity`) mapeadas a tablas de MySQL.

### 3.1 Diagrama de Componentes
> **[INSERTE AQUÍ: Captura de pantalla del diagrama de componentes o estructura de paquetes en NetBeans]**
> *Ruta sugerida: docs/img/diagrama_componentes.png*

## 4. Estructura del Proyecto
*   `src/main/java`: Código fuente Java.
*   `src/main/webapp`: Páginas web, recursos CSS/JS y configuración WEB-INF.
*   `src/test`: Pruebas unitarias con JUnit y Mockito.

## 5. Configuración de Base de Datos
La conexión se define en `glassfish-resources.xml` y `persistence.xml`.

### 5.1 Esquema E-R
> **[INSERTE AQUÍ: Captura del diagrama Entidad-Relación (DER) generado por Workbench o MySQL]**
> *Ruta sugerida: docs/img/der_database.png*

## 6. Proceso de Despliegue (SaveInCloud/GlassFish)
Pasos técnicos para poner el sistema en producción:

1.  Generar el WAR: `mvn clean package`.
2.  Renombrar a `ROOT.war` (opcional).
3.  Subir al servidor GlassFish a través de la consola de administración o dashboard de SaveInCloud.
4.  Configurar variables de entorno (`SENDGRID_API_KEY`).

## 7. Interfaces (APIs)
El sistema consume la API de SendGrid para el envío de correos.

> **[INSERTE AQUÍ: Captura de la clase `ServicioCorreo.java` o la configuración de SendGrid]**
