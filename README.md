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

**Módulos del Proyecto:**

- **Módulo de Autenticación:** Registro e inicio de sesión.
- **Módulo de Registro de Glucosa:** Permite ingresar, visualizar y editar los niveles de glucosa.
- **Módulo de Medicamentos:** Configuración y recordatorio de medicamentos.
- **Módulo de Anomalías:** Registro de eventos como hipoglucemia o hiperglucemia.
- **Módulo de Recordatorios Automáticos:** Configuración personalizada, notificaciones con sonido y vibración.
- Modulo de citas

**Diagrama de Navegación:**

- Pantalla de Inicio de Sesión.
- Dashboard con accesos a las funciones principales.
- Registro de Glucosa.
- Configuración de Recordatorios.
- Visualización de Historial.
- Gráficos de Evolución.
- Reporte de Anomalías.
