# Requisitos Funcionales y Criterios de Aceptación

Fecha: 2025-11-25
Proyecto: Check.Inc — Sistema de Gestión Médica (Diabetes)
Autor: Equipo de desarrollo

---

## 1. Introducción
Este documento consolida los Requisitos Funcionales (RF) del sistema, añade requisitos faltantes detectados durante la implementación, define criterios de aceptación (CA), y mapea requisitos a artefactos del código (trazabilidad). También incluye requisitos no-funcionales y casos de prueba sugeridos.

Objetivo: centralizar la especificación de comportamiento y aceptación para facilitar pruebas, auditoría y despliegue.

---

## 2. Actores
- Paciente: usuario normal que registra datos personales, glucosa, medicamentos, anomalías y citas.
- Administrador: gestiona usuarios y puede eliminar cuentas.
- Sistema (servicios automáticos): envío de notificaciones, generación de alertas y creación automática de anomalías.

---

## 3. Requisitos Funcionales (actualizados)
Se mantienen los RF originales y se añaden nuevos RF para cubrir notificaciones, reportes, importación, auditoría y gestión de rangos.

Módulo Usuarios
- RF-01 Registrar Usuario: El sistema permite que un paciente se registre en la aplicación.
  - Actor: Paciente
  - Permisos: Público
- RF-02 Consultar Usuario: Visualizar información de perfil.
  - Actor: Paciente
  - Permisos: Paciente (su propio perfil), Admin (todos)
- RF-03 Modificar Usuario: Actualizar información personal.
  - Actor: Paciente
  - Permisos: Paciente (su propio perfil), Admin
- RF-04 Eliminar Usuario: Eliminar cuentas de pacientes.
  - Actor: Administrador
  - Permisos: Admin

Módulo Glucosa
- RF-05 Registrador Nivel de Glucosa: Registrar niveles con fecha y hora.
  - Actor: Paciente
- RF-06 Consultar Historial de Glucosa: Visualizar historial y filtrar por mes/periodo.
  - Actor: Paciente
- RF-07 Modificar Registro de Glucosa: Editar una medición registrada.
  - Actor: Paciente
- RF-08 Eliminar Registro de Glucosa: Eliminar un registro.
  - Actor: Paciente

Medicamentos
- RF-09 Registrar Medicamentos: Registrar medicamento con nombre, dosis y frecuencia.
  - Actor: Paciente
- RF-10 Consultar Medicamentos: Ver lista de medicamentos registrados.
  - Actor: Paciente
- RF-11 Modificar Medicamento: Actualizar datos.
  - Actor: Paciente
- RF-12 Eliminar Medicamento: Eliminar medicamento.
  - Actor: Paciente

Módulo Anomalías
- RF-13 Registrar Anomalías: Registrar anomalías con fecha, síntomas y observaciones.
  - Actor: Paciente
- RF-14 Consultar Anomalías: Visualizar historial de anomalías.
  - Actor: Paciente
- RF-15 Modificar Anomalía: Actualizar detalles de una anomalía.
  - Actor: Paciente
- RF-16 Eliminar Anomalía: Eliminar registro.
  - Actor: Paciente

Módulo Citas
- RF-17 Registrar Cita Médica: Registrar cita con fecha, hora y motivo.
  - Actor: Paciente
- RF-18 Consultar Citas Médicas: Visualizar citas programadas.
  - Actor: Paciente
- RF-19 Modificar Cita Médica: Actualizar detalles.
  - Actor: Paciente
- RF-20 Eliminar Cita Médica: Cancelar/eliminar cita.
  - Actor: Paciente

Nuevos requisitos sugeridos
- RF-21 Notificaciones por correo: En registrar lectura crítica, enviar correo al paciente y/o contacto de emergencia.
  - Actor: Sistema
  - Permisos: Automático
- RF-22 Registro de notificación: Registrar intento de notificación (`alerta`), destino y resultado (éxito/fallo).
  - Actor: Sistema
- RF-23 Generar reportes de glucosa: Generar reportes mensuales (PDF/CSV) con gráficas y agrupación por mes.
  - Actor: Paciente, Admin
- RF-24 Importar lecturas (CSV/XLS): Permitir importar lecturas de glucosa.
  - Actor: Paciente
- RF-25 Gestión de rangos personalizados (`NivelesGlucosa`): Permitir configurar rangos por usuario.
  - Actor: Paciente
- RF-26 Auditoría: Registrar usuario, acción y timestamp para operaciones críticas (crear/eliminar usuario, anomalía crítica).
  - Actor: Sistema
- RF-27 Gestión de roles/privilegios: Definir y aplicar permisos (admin/paciente).
  - Actor: Admin
- RF-28 Manejo de errores de correo: Reintentos y fallback (guardar intento y notificar admin si falla repetidamente).
  - Actor: Sistema
- RF-29 Exportar anomalías: Permitir descargar historial de anomalías (CSV/PDF).
  - Actor: Paciente, Admin
- RF-30 Configuración de emergencia: Permitir definir contacto de emergencia y preferencias de notificación.
  - Actor: Paciente

---

## 4. Criterios de Aceptación (por RF clave)
Cada RF debe tener criterios claros para pruebas de aceptación.

RF-05 - Registrador Nivel de Glucosa
- CA-05.1: Al guardar una lectura válida (nivel numérico, fecha/hora y usuario), la fila se persiste en tabla `glucosa`.
- CA-05.2: Validaciones: `nivelGlucosa` > 0, `fechaHora` no nula.
- CA-05.3: Si `nivelGlucosa` cae en estado CRÍTICO (según `NivelesGlucosa`), se crea `anomalia` y se intenta enviar `alerta`.

RF-06 - Consultar Historial de Glucosa
- CA-06.1: El paciente ve sus lecturas ordenadas por `fechaHora` descendente.
- CA-06.2: Filtros por rango de fechas y por mes devuelven subconjunto correcto.

RF-21 - Notificaciones por correo
- CA-21.1: Al registrar lectura CRÍTICA se realiza intento de envío de correo.
- CA-21.2: Se registra una fila en `alerta` con `contenido` describiendo destino y resultado.
- CA-21.3: Si envío falla, el sistema registra el fallo y, tras N reintentos, marca la incidencia para revisión.

RF-23 - Generar reportes de glucosa
- CA-23.1: Botón de descarga produce PDF que incluye: título, rango de fechas, tabla de lecturas, gráfico de tendencia y agrupación por mes.
- CA-23.2: CSV export contiene cabeceras y valores exactos.

RF-25 - Gestión de rangos personalizados
- CA-25.1: El usuario puede crear/editar/desactivar su `NivelesGlucosa`.
- CA-25.2: Al editar, futuras determinaciones de estado usan el nuevo rango.

RF-26 - Auditoría
- CA-26.1: Todas las acciones críticas quedan registradas con `usuario`, `acción`, `timestamp`, `resultado`.

---

## 5. Matriz de Permisos (resumen)
- Paciente: RF-01, RF-02, RF-03, RF-05..RF-12, RF-13..RF-16, RF-17..RF-20, RF-23 (su propio reporte), RF-24, RF-25, RF-29, RF-30.
- Administrador: Todos los anteriores + RF-04, RF-27, RF-26 (revisión auditoría).
- Sistema (servicios): RF-21, RF-22, RF-26, RF-28.

---

## 6. Trazabilidad (RF → Artefactos del código)
Mapeo rápido de RFs a clases, páginas y tablas (ejemplos identificados en el repo actual):

- RF-01..RF-04 -> `src/main/java/.../entities/Usuario.java`, `UsuarioFacadeLocal`, páginas: `views/usuarios/*.xhtml`.
- RF-05..RF-08 -> `Glucosa`, `GlucosaFacadeLocal`, `RegistroGlucosa` (bean), UI: `views/glucosa/registroGlucosa.xhtml`.
- RF-09..RF-12 -> `Medicamento`, `MedicamentoFacadeLocal`, `views/usuarios/medicamentos.xhtml`.
- RF-13..RF-16 -> `Anomalia`, `AnomaliaFacadeLocal`, UI: `views/anomalias/*.xhtml`.
- RF-17..RF-20 -> `Cita`, `CitaFacadeLocal`, UI: `views/citas/*.xhtml`.
- RF-21 -> `ServicioCorreo.enviarAlertaGlucosaHTML()`, `RegistroGlucosa.crearAnomaliaGlucosa()` y tabla `alerta`.
- RF-22 -> tabla `alerta` (insertar al enviar correos), servicio: `ServicioCorreo`.
- RF-23 -> `ReporteBean`, `ReporteGeneralPDF`, util: `ReporteBasePDF.crearGraficoGlucosa()`.
- RF-24 -> `ImportarGlucosaService`.
- RF-25 -> `NivelesGlucosa` entity + `NivelesGlucosaFacade`.
- RF-26 -> Hook en facades/beans o interceptor para registrar auditoría (no implementado por defecto).

> Recomendación: completar la tabla de trazabilidad en `docs/traceability.csv` con columnas: RF, descripción, archivo.java, página.xhtml, tabla.sql, test-case-id.

---

## 7. Requisitos No-Funcionales (resumen)
- RNF-01 Seguridad: Contraseñas hashed con bcrypt, HTTPS obligatorio en producción.
- RNF-02 Disponibilidad: Backups diarios, RTO < 4h.
- RNF-03 Rendimiento: Consultas de historial < 1s para ≤ 1000 registros por usuario.
- RNF-04 Internacionalización: Fechas en formato configurable (dd/MM/yyyy por defecto).
- RNF-05 Privacidad: Retención de datos configurable y opción de exportar/darse de baja.
- RNF-06 Observabilidad: Logs estructurados, métricas de envíos de correo y errores.

---

## 8. Casos de Prueba / Escenarios (selectos)
Adjunto ejemplos rápidos que puedes convertir a pruebas automáticas (JUnit + Arquillian o selenium/selenide para UI):

- CP-01 Crear usuario válido → comprobar `usuario` en DB y login funcional.
- CP-02 Registrar lectura normal → `glucosa` creada; no se crea `anomalia`.
- CP-03 Registrar lectura crítica → `glucosa` + `anomalia` + intento de `alerta`.
- CP-04 Forzar fallo SendGrid (API key inválida) → `alerta` con contenido indicando fallo y retry policy activada.
- CP-05 Generar reporte 6 meses → PDF descargado; validar que contiene 6 secciones por mes.
- CP-06 Importar CSV con 50 lecturas → validar 50 filas insertadas y excepción controlada si hay rows inválidas.

---

## 9. Notas de Implementación y Despliegue
- Variables de entorno críticas:
  - `SENDGRID_API_KEY` (o `Config.get("SENDGRID_API_KEY")` en `ServicioCorreo`).
  - Datasource JNDI: `java:app/jdbc/checks` definido en `persistence.xml`.
- `persistence.xml` debe listar todas las entidades usadas: incluye `NivelesGlucosa`, `Anomalia`, `Alerta`.
- GlassFish: al desplegar, hacer `undeploy` y `deploy` para evitar caché de PU.

---

## 10. Próximos pasos recomendados
1. Aceptar y versionar este documento en `docs/requirements_funcionales.md`.
2. Generar `docs/traceability.csv` con mapeo RF -> archivos exactos.
3. Implementar RF-22 (persistir intentos de notificación) si no está ya.
4. Añadir pruebas automáticas para CA-05.1/CA-21.1/CA-23.1.
5. Añadir un pequeño interceptor para auditoría (RF-26).

---

## 11. Contacto
Si quieres, puedo:
- Crear `docs/traceability.csv` automáticamente con las clases que identifiqué.
- Añadir las pruebas de aceptación básicas (esqueleto JUnit).
- Generar la entrada de RF-22 en código (`ServicioCorreo` + `AlertaFacade`).

Dime qué quieres que haga a continuación (crear traceability, generar tests, o crear commit con este archivo).