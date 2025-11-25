Plan de Pruebas — Proyecto CheckInc

1. Objetivo
- Verificar la corrección, seguridad y usabilidad de las funcionalidades desarrolladas: registro/login, registro de glucosa, sistema de alertas (envío y persistencia), generación de reportes (glucosa y citas), descarga de reportes, y panel de alertas.

2. Alcance
- Incluye pruebas funcionales, de integración, y pruebas de aceptación de usuario (UAT) para los módulos mencionados.
- No incluye pruebas de rendimiento a gran escala ni pruebas de ruptura de la base de datos (se pueden planear en otra fase).

3. Entorno de Prueba
- Servidor de aplicación: GlassFish / TomEE (mismo que producción recomendado).
- Base de datos: MySQL/MariaDB (usar `Db/checks.sql` como dataset de preparación).
- Variables de entorno: `SENDGRID_API_KEY` debe estar configurada para pruebas de envío (usar API de pruebas o mock si se desea evitar costes).
- Maven: Java 8 (compilación actual del proyecto).

4. Roles
- QA: ejecutar casos de prueba y registrar evidencias.
- Desarrollador: resolver defectos y desplegar arreglos.
- Usuario (cliente): validar criterios de aceptación en UAT.

5. Criterios de Entrada
- Build exitoso: `mvn -DskipTests package` sin errores.
- BD cargada con `Db/checks.sql` y el datasource configurado en `persistence.xml`.
- Variable `SENDGRID_API_KEY` configurada (o usar clave de pruebas/mocks).

6. Criterios de Salida
- Todos los casos críticos pass (sin defectos bloqueantes abiertos).
- UAT: aprobaciones firmadas para las funcionalidades solicitadas.

7. Herramientas
- Git para control de versiones.
- Maven para build y empaquetado.
- Browser (Chrome/Firefox) para pruebas UI.
- Postman (opcional) para probar endpoints REST si existen.

8. Plan y Calendario (resumen)
- Día 1: Preparación entorno + carga `checks.sql`.
- Día 2: Pruebas funcionales básicas (login, registro, CRUD usuarios, reportes básicos).
- Día 3: Pruebas alertas + validación envíos y persistencia en `alerta`.
- Día 4: Pruebas UAT por usuario-cliente + correcciones.

9. Riesgos
- SendGrid puede fallar por límites de cuenta → usar mock o clave de pruebas.
- Diferencias entre entorno local y producción (datasource JNDI) → documentar configuración.

10. Matriz de Prioridad
- Criticas: registro de glucosa, creación de anomalia, envío persistente de alertas.
- Altas: generación y descarga de reportes mensuales, routing de descarga.
- Medias: panel `verAlertas.xhtml`, UI de personalización de rangos (pendiente).

11. Reporte de Incidencias
- Definir plantilla de bug: ID, resumen, pasos para reproducir, resultado esperado, resultado actual, severidad, capturas/logs, responsable.

12. Aprobación
- El Plan de Pruebas lo aprueba el responsable de QA y el Product Owner antes de ejecutar UAT.
