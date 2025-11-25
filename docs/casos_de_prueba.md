Casos de Prueba — CheckInc

Instrucciones: ejecutar cada caso, documentar evidencia (pantallas, logs, registros DB) y marcar Pass/Fail.

1) Login de usuario (CRÍTICO)
- Precondición: usuario con credenciales válidas existe (usar `usuario` id 1 del `checks.sql`).
- Pasos:
  1. Abrir `/views/usuarios/login.xhtml`.
  2. Ingresar `user` y `password` correctos.
  3. Presionar 'Entrar'.
- Resultado esperado: Redirección al dashboard, sesión iniciada, no hay exposición de contraseña en logs.

2) Registro de usuario (ALTA)
- Pasos:
  1. Ir al formulario de registro `registrousuario.xhtml`.
  2. Completar formulario con datos válidos.
  3. Enviar.
- Resultado esperado: Nuevo usuario creado en tabla `usuario` con `password` hasheado (bcrypt), correo de bienvenida enviado (o auditado en `alerta`).

3) Registro de glucosa — Normal (CRÍTICO)
- Precondición: usuario autenticado.
- Pasos:
  1. Ir a registro glucosa (`registroGlucosa.xhtml`).
  2. Ingresar nivel dentro del rango normal para el `tipoDiabetes` del usuario.
  3. Enviar.
- Resultado esperado: registro guardado en `glucosa`, no se crea `anomalia`, no se envía alerta (o se registra un intento con resultado que indique no necesario).

4) Registro de glucosa — Bajo leve (ALTA)
- Pasos: registrar nivel entre `nivelBajoCritico` y `nivelMinimo`.
- Resultado esperado: registro en `glucosa`, `NivelesGlucosaFacade` devuelve estado `BAJO`, se genera recomendación; optionally send alert (según la regla), y si se envía, debe quedar registrado en `alerta`.

5) Registro de glucosa — Crítico bajo (CRÍTICO)
- Pasos: ingresar nivel <= `nivelBajoCritico`.
- Resultado esperado: se crea `anomalia` con `gravedad=grave`, se intenta envío de alerta por correo; existe entrada en `alerta` con `tipo`=ALERTA_GLUCOSA y contenido con el código/respuesta del proveedor.

6) Registro de glucosa — Alto / Crítico alto (ALTA/CRÍTICO)
- Similar a 4/5: validación de estados `ALTO` y `CRITICO_ALTO`, creación de anomalia si es crítico y registro de alertas.

7) Persistencia de alertas (CRÍTICO)
- Pasos:
  1. Forzar un envío de alerta (registro crítico) con `SENDGRID_API_KEY` inválida para provocar error.
  2. Verificar que existe una fila en `alerta` con el `fechaHora` del intento y `contenido` con el código/mensaje.
- Resultado esperado: se registra al menos un intento en la tabla `alerta`.

8) Ver panel de alertas en UI (ALTA)
- Pasos:
  1. Acceder a `/views/admin/verAlertas.xhtml` como admin.
- Resultado esperado: la tabla muestra alertas recientes con `tipo`, `contenido`, `fechaHora`, `visto` y usuario asociado.

9) Generación de reporte de glucosa agrupado por mes (ALTA)
- Pasos:
  1. Ir al módulo de reportes y solicitar reporte de glucosa en un rango que incluya varios meses.
  2. Verificar gráfico y datos.
- Resultado esperado: datos agrupados por mes, gráfico renderizado y botón de descarga exporta sólo el reporte seleccionado (glucosa).

10) Generación y descarga de reporte de citas agrupado por mes (ALTA)
- Pasos similares al 9.
- Resultado esperado: descarga del reporte de citas, sin mezclar con reporte de glucosa.

11) Seguridad: secretos no en repo (CRÍTICO)
- Pasos: revisar `src/main/resources/config.properties` y confirmar que no contiene claves sensibles; verificar `Config` lee variables de entorno.
- Resultado esperado: no hay claves en repo; `SENDGRID_API_KEY` debe ser provista por variable de entorno.

12) Integración y Build (BÁSICA)
- Pasos: ejecutar `mvn -DskipTests package`.
- Resultado esperado: BUILD SUCCESS.

Notas adicionales
- Para pruebas de correo, usar cuenta de pruebas de SendGrid o mock local (MailHog) y revisar `alerta` para auditoría.
- Registrar evidencias (logs, capturas, SELECT en DB) para cada caso Fallido.
