### Maquetación del Proyecto de Grado - App para la Gestión de la Diabetes

#### 1. ¿Qué se va a hacer? (Alcance)
**Descripción General:**
Desarrollo de una aplicación móvil para la autogestión de la diabetes, orientada a pacientes que deseen llevar su propio registro de salud sin intervención médica.

**Funcionalidades Principales:**
- Registro de glucosa diaria.
- Registro de medicamentos.
- Registro de anomalías (hipoglucemia, hiperglucemia).
- Configuración de recordatorios automáticos para medicamentos y citas.
- Notificaciones con sonido y vibración.
- Visualización de historial de recordatorios completados y pendientes.
- Gráficos para mostrar la evolución de la glucosa.

**Público Objetivo:**
Pacientes con diabetes que deseen llevar un control personalizado de su salud de manera autónoma.

---

#### 2. ¿Por qué se va a hacer? (Justificación)
**Problema a Resolver:**
La falta de herramientas accesibles que permitan a los pacientes con diabetes gestionar su propia salud sin depender de terceros.

**Beneficios:**
- Facilitar la autogestión de la diabetes.
- Mejorar la adherencia al tratamiento mediante recordatorios.
- Ofrecer una herramienta digital accesible y fácil de usar.
- Permitir la visualización de la evolución de la glucosa de manera gráfica.

**Diferenciación:**
A diferencia de otras aplicaciones, esta app se centra exclusivamente en la autogestión del paciente, eliminando la intervención médica.

---

#### 3. ¿Cómo se va a hacer? (Plan de Desarrollo)
**Arquitectura de la App:**
- Frontend: Flutter.
- Backend: PHP.
- Base de Datos: MySQL Workbench.

**Módulos del Proyecto y Endpoints en PHP:**

- **Módulo de Autenticación (`auth.php`)**
  - `POST /registro` → Registrar usuario.
  - `POST /login` → Iniciar sesión.
  - `GET /logout` → Cerrar sesión.

- **Módulo de Registro de Glucosa (`glucosa.php`)**
  - `POST /glucosa` → Registrar nivel de glucosa.
  - `GET /glucosa/{id}` → Obtener datos de glucosa de un usuario.
  - `PUT /glucosa/{id}` → Editar un registro de glucosa.
  - `DELETE /glucosa/{id}` → Eliminar un registro.

- **Módulo de Medicamentos (`medicamentos.php`)**
  - `POST /medicamento` → Agregar un medicamento.
  - `GET /medicamento/{id}` → Obtener medicamentos de un usuario.
  - `PUT /medicamento/{id}` → Editar un medicamento.
  - `DELETE /medicamento/{id}` → Eliminar un medicamento.

- **Módulo de Anomalías (`anomalias.php`)**
  - `POST /anomalia` → Registrar evento de hipoglucemia o hiperglucemia.
  - `GET /anomalia/{id}` → Consultar anomalías registradas.
  - `DELETE /anomalia/{id}` → Eliminar un evento registrado.

- **Módulo de Recordatorios Automáticos (`recordatorios.php`)**
  - `POST /recordatorio` → Crear un recordatorio.
  - `GET /recordatorio/{id}` → Obtener recordatorios de un usuario.
  - `PUT /recordatorio/{id}` → Modificar un recordatorio.
  - `DELETE /recordatorio/{id}` → Eliminar un recordatorio.

- **Módulo de Citas (`citas.php`)**
  - `POST /cita` → Agendar una cita.
  - `GET /cita/{id}` → Obtener citas de un usuario.
  - `PUT /cita/{id}` → Editar una cita.
  - `DELETE /cita/{id}` → Eliminar una cita.

**Diagrama de Navegación:**
- Pantalla de Inicio de Sesión.
- Dashboard con accesos a las funciones principales.
- Registro de Glucosa.
- Configuración de Recordatorios.
- Visualización de Historial.
- Gráficos de Evolución.
- Reporte de Anomalías.



