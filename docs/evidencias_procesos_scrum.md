# Guía de Evidencias de Procesos y Scrum

Esta guía te ayudará a generar las evidencias visuales requeridas para la documentación del proyecto.

## 1. Diagramas de Procesos (BPMN / Bizagi)

Debes crear dos diagramas usando una herramienta como **Bizagi Modeler**.

### 1.1 Diagrama del Proceso "Anterior" (Situación SIN Software)
Representa cómo se hacía el proceso manualmente antes de tu sistema.
*   **Actores**: Paciente, Médico/Enfermera.
*   **Flujo Típico**:
    1.  Paciente mide su glucosa en casa y la anota en un cuaderno (o no la anota).
    2.  Paciente olvida el cuaderno o lo pierde.
    3.  Médico pregunta por registros en la consulta.
    4.  Médico no tiene datos fiables -> Diagnóstico difícil.
*   **Captura**: Exporta el diagrama como imagen y guárdalo en `docs/img/proceso_anterior.png`.

### 1.2 Diagrama del Proceso "Actual" (Situación CON Check.Inc)
Representa el nuevo flujo optimizado.
*   **Actores**: Paciente, Sistema Check.Inc, Médico.
*   **Flujo Típico**:
    1.  Paciente mide glucosa -> Registra en App Web.
    2.  Sistema guarda datos y verifica si es crítico -> Envía alerta si es necesario.
    3.  Médico inicia sesión antes de la cita -> Revisa gráficos históricos.
    4.  Cita médica informada con datos precisos.
*   **Captura**: Exporta el diagrama como imagen y guárdalo en `docs/img/proceso_actual.png`.

## 2. Evidencias de Scrum (Gestión)

Debes tomar capturas de tu herramienta de gestión (Jira, Trello, Planner o GitHub Projects).

### 2.1 Backlog del Producto
> **[INSERTE AQUÍ: Captura del backlog con todas las historias de usuario listadas]**

### 2.2 Tablero Kanban (Sprint en curso)
> **[INSERTE AQUÍ: Captura del tablero con columnas "To Do", "In Progress", "Done"]**
> Asegúrate de que se vean tareas moviéndose.

### 2.3 Historias de Usuario Detalladas
> **[INSERTE AQUÍ: Captura de una historia de usuario abierta mostrando criterios de aceptación]**

## 3. Pruebas de Software

### 3.1 Pruebas Unitarias (JUnit)
Ejecuta los tests en tu IDE (NetBeans) y toma captura de los resultados en verde.
> **[INSERTE AQUÍ: Captura de "Test Results" en NetBeans con barra verde al 100%]**

### 3.2 Plan de Pruebas
Asegúrate de tener lleno el archivo `docs/plan_de_pruebas.md`.

### 3.3 Informe de Aceptación
Completa `docs/informe_aceptacion_usuario.md` con la firma (digital o simulada) del "cliente" aceptando el software.
