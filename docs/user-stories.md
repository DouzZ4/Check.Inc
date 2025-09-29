# Historias de Usuario — Check.Inc

Fecha: 2025-09-29

Este documento contiene las historias de usuario para las funcionalidades principales de la aplicación Control Glucosa (módulos: Autenticación, Usuarios, Lectura de Glucosa, Citas, Reportes, Notificaciones, Administración). Cada historia incluye: descripción (formato "Como <rol>..."), criterios de aceptación, prioridad y estimación en puntos.

---

## 1. Autenticación

### HU-01: Login de usuario
- Como: Usuario registrado
- Quiero: poder iniciar sesión con correo/usuario y contraseña
- Para: acceder a mi panel y gestionar mis registros de glucosa y citas

Criterios de aceptación:
- El formulario acepta correo/usuario y contraseña.
- Si las credenciales son correctas, el usuario es redirigido a `views/usuarios/Dashboard.xhtml` (o panel apropiado).
- Si las credenciales son incorrectas, se muestra un mensaje de error claro y no se inicia sesión.
- Las sesiones expiran tras un periodo configurable.

Prioridad: Alta
Estimación: 3 puntos

### HU-02: Registro de nuevo usuario (autenticación + perfil)
- Como: Usuario no registrado
- Quiero: crear una cuenta con datos personales y credenciales
- Para: poder acceder a la aplicación y registrar mis mediciones

Criterios de aceptación:
- Formulario `registrousuario.xhtml` valida campos obligatorios (nombres, apellidos, documento, correo, contraseña, edad, tipo de diabetes cuando aplica).
- Se crea un `Usuario` en la base de datos y se envía un correo de bienvenida (envío real o en cola).
- Tras registro, el usuario puede iniciar sesión.
- Mensajes de error claros para duplicados o formato inválido.

Prioridad: Alta
Estimación: 5 puntos

---

## 2. Gestión de Usuarios

### HU-03: Ver perfil de usuario
- Como: Usuario autenticado
- Quiero: ver mis datos personales y configuración (correo, teléfono, tipo de diabetes)
- Para: verificar y actualizar mi información

Criterios de aceptación:
- Página muestra datos del `Usuario` actual.
- Existe botón para editar datos y guardar cambios con validación.
- Cambios persisten en la BD y aparecen inmediatamente.

Prioridad: Media
Estimación: 3 puntos

### HU-04: Gestión de usuarios (Admin)
- Como: Administrador
- Quiero: listar, editar y eliminar usuarios desde `admin_gestionar_usuarios.xhtml`
- Para: mantener la base de usuarios limpia y gestionar roles

Criterios de aceptación:
- El admin puede ver una tabla con todos los usuarios.
- El admin puede editar datos y asignar roles.
- El admin puede eliminar usuarios con confirmación.
- Acceso protegido solo para administradores.

Prioridad: Alta
Estimación: 8 puntos

---

## 3. Registro de Glucosa

### HU-05: Registrar medición de glucosa
- Como: Usuario autenticado
- Quiero: registrar una medición con nivel (mg/dL), fecha/hora y momento del día
- Para: llevar control de mis tendencias de glucosa

Criterios de aceptación:
- Formulario `registroGlucosa.xhtml` valida `nivelGlucosa`, `fechaHora` y `momentoDia`.
- Registro persiste en la tabla `Glucosa` asociada al `Usuario`.
- Cuando hay registros, se muestra un gráfico de tendencia (`p:lineChart`).

Prioridad: Alta
Estimación: 5 puntos

### HU-06: Filtrar y exportar registros de glucosa
- Como: Usuario autenticado
- Quiero: filtrar por fecha, nivel o momento del día y exportar a PDF/CSV
- Para: analizar mis datos fuera de la plataforma

Criterios de aceptación:
- Filtros aplican correctamente y actualizan la tabla.
- Exportación genera un archivo descargable con los registros filtrados.
- El PDF incluye un gráfico si hay suficientes datos.

Prioridad: Media
Estimación: 5 puntos

---

## 4. Citas

### HU-07: Agendar una cita
- Como: Usuario autenticado
- Quiero: crear una cita con fecha, hora y motivo
- Para: reservar atención con el profesional

Criterios de aceptación:
- Formulario permite escoger fecha/hora válidas.
- No se permiten solapamientos si política lo requiere.
- La cita se guarda y aparece en la lista de `Citas Programadas`.

Prioridad: Medium
Estimación: 5 puntos

### HU-08: Ver y cancelar citas
- Como: Usuario autenticado
- Quiero: ver mis próximas citas y cancelar si es necesario
- Para: gestionar mi agenda

Criterios de aceptación:
- Lista de citas muestra fecha, hora y motivo.
- El usuario puede cancelar una cita con confirmación.
- La cancelación notifica al administrador/medico (si aplica).

Prioridad: Medium
Estimación: 3 puntos

---

## 5. Reportes

### HU-09: Generar reporte completo del paciente (PDF)
- Como: Usuario autenticado
- Quiero: descargar un reporte PDF con mis datos, historial de glucosa, medicamentos y citas
- Para: compartir con profesionales o conservar registros

Criterios de aceptación:
- El reporte incluye encabezado con datos del paciente.
- Se generan secciones: Glucosa (tabla + gráfico), Medicamentos, Citas.
- PDF se descarga sin errores y respeta formato.

Prioridad: High
Estimación: 8 puntos

### HU-10: Exportar listados de usuarios (Admin)
- Como: Administrador
- Quiero: exportar la lista de usuarios a CSV/XLS
- Para: generar reportes institucionales

Criterios de aceptación:
- Export contiene columnas clave (nombre, correo, documento, edad, tipo de diabetes).
- Solo accesible por administrador.

Prioridad: Medium
Estimación: 3 puntos

---

## 6. Notificaciones / Correo

### HU-11: Enviar correo de bienvenida al registrarse
- Como: Sistema
- Quiero: enviar un correo de bienvenida cuando un usuario se registra
- Para: confirmar registro y dar información inicial

Criterios de aceptación:
- Al registrar un usuario, se invoca `ServicioCorreo.enviarCorreoRegistro`.
- El correo contiene fecha, datos básicos y credenciales (si aplica) o link de activación.
- Si falla el envío, se registra en logs y no bloquea el registro (reintento/cola opcional).

Prioridad: High
Estimación: 3 puntos

### HU-12: Enviar comunicados masivos (Admin)
- Como: Administrador
- Quiero: enviar correos masivos a grupos o a todos los usuarios
- Para: informar campañas, mantenimientos o novedades

Criterios de aceptación:
- Interfaz para redactar asunto y mensaje.
- Envío en background o por lotes con reportes de éxito/fallo.
- El administrador puede ver el historial de envíos.

Prioridad: Medium
Estimación: 8 puntos

---

## 7. Seguridad y Auditoría

### HU-13: Registrar acciones críticas en log/auditoría
- Como: Administrador
- Quiero: tener trazabilidad de acciones como creación/eliminación de usuarios y envíos masivos
- Para: cumplir requisitos legales y detectar problemas

Criterios de aceptación:
- Eventos críticos quedan en tabla de auditoría o archivo de logs con usuario, fecha y acción.
- Vista para administradores con filtros básicos.

Prioridad: Medium
Estimación: 5 puntos


