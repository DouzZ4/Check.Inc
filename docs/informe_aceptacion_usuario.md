Informe de Aceptación de Usuario (UAT) — CheckInc

Proyecto: CheckInc
Responsable QA: __________________
Responsable PO (Usuario): __________________
Fecha de ejecución: _________________

1. Resumen ejecutivo
- Se ejecutaron los casos de prueba definidos en el Plan de Pruebas y Casos de Prueba. Las funcionalidades críticas son: registro/login, registro de glucosa y sistema de alertas (envío y auditoría), creación de anomalías para lecturas críticas, generación y descarga de reportes agrupados por mes, y panel de alertas.

2. Alcance de UAT
- Validar que la aplicación cumple las historias de usuario y criterios de aceptación definidos en `docs/requirements_funcionales.md` referentes a los items implementados.

3. Resultado resumen
- Total casos ejecutados: [__]
- Casos Pasados: [__]
- Casos Fallidos: [__]
- Defectos críticos abiertos: [__]
- Defectos mayores abiertos: [__]

4. Detalle de incidencias críticas (ejemplos)
- ID: UAT-01
  - Resumen: Envío de alerta falla con 401 (SendGrid) — resultado: alerta registrada en BD. Estado: mitigar (rotar clave o usar cuenta activa).
  - Severidad: Crítica.
  - Acciones recomendadas: Proveer credenciales de prueba válidas, o usar mock para UAT.

- ID: UAT-02
  - Resumen: (Si hubiese otro hallazgo durante la ejecución se documenta aquí)

5. Criterios de Aceptación
- Criterio 1: Registro de glucosa genera `anomalia` y desencadena alerta cuando el valor es crítico — Resultado: [APROBADO/NO APROBADO]
- Criterio 2: Las alertas quedan registradas en la tabla `alerta` con fechaHora y contenido — Resultado: [APROBADO/NO APROBADO]
- Criterio 3: Los reportes se generan agrupados por mes y la descarga exporta el reporte solicitado — Resultado: [APROBADO/NO APROBADO]
- Criterio 4: No hay secretos en el repo; `SENDGRID_API_KEY` es variable de entorno — Resultado: [APROBADO/NO APROBADO]

6. Recomendaciones previas al sign-off
- Rotar/usar cuenta SendGrid de pruebas o configurar un mock de SMTP para evitar fallos por límites de cuenta.
- Añadir pruebas automáticas de integración para el flujo de alertas (con OkHttp mock).
- (Opcional) Añadir panel para marcar alertas como vistas y un endpoint para reintento manual.

7. Aceptación
- Firma Usuario / Product Owner: ______________________  Fecha: __________
- Firma QA: ______________________  Fecha: __________

Anexos: evidencias (logs, capturas pantalla, consultas SQL).