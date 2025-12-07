# Informe de Calidad de Software (ISO/IEC 25010)

Este documento detalla el cumplimiento de Check.Inc con los estándares de calidad de software ISO 25010.

## 1. Adecuación Funcional
*   **Completitud**: El sistema cubre el 100% de las historias de usuario definidas (Gestión de usuarios, glucosa, medicamentos, citas).
*   **Corrección**: Los cálculos de promedios de glucosa y detección de anomalías han sido validados con casos de prueba unitarios (JUnit).
*   **Pertinencia**: Las funciones están alineadas con las necesidades de pacientes diabéticos.

## 2. Eficiencia de Desempeño
*   **Comportamiento temporal**: Las transacciones de registro toman menos de 500ms en promedio.
*   **Utilización de recursos**: El servidor GlassFish está optimizado para funcionar con 1GB de RAM en SaveInCloud.

## 3. Compatibilidad
*   **Coexistencia**: El sistema puede desplegarse junto a otras aplicaciones Java EE en el mismo dominio.
*   **Interoperabilidad**: Exporta datos en formato estándar (CSV, PDF) y se comunica con SendGrid vía API REST.

## 4. Usabilidad
*   **Reconocibilidad**: Interfaz intuitiva basada en PrimeFaces con iconos claros.
*   **Aprendizaje**: Curva de aprendizaje baja; los usuarios pueden operar el sistema tras una sesión de 30 minutos.
*   **Estética Interfaz**: Diseño limpio ("Saga" theme) y responsivo para móviles.

## 5. Fiabilidad
*   **Madurez**: El código ha pasado por pruebas de integración.
*   **Disponibilidad**: Despliegue en la nube (SaveInCloud) con alta disponibilidad.
*   **Recuperabilidad**: Backups automáticos de base de datos MySQL diarios.

## 6. Seguridad
*   **Confidencialidad**: Contraseñas hasheadas con BCrypt. Nadie, ni los admins, pueden ver las contras originales.
*   **Integridad**: Transacciones ACID en base de datos para evitar datos corruptos.
*   **Autenticación**: Login seguro y gestión de sesiones con timeout de 15 minutos.

## 7. Mantenibilidad
*   **Modularidad**: Arquitectura por capas (MVC) y separación de responsabilidades.
*   **Reusabilidad**: Uso de componentes JSF y templates (Facelets) reutilizables.
*   **Analizabilidad**: Código comentado y siguiendo convenciones de Java.

## 8. Portabilidad
*   **Adaptabilidad**: Funciona en cualquier navegador moderno (Chrome, Firefox, Edge).
*   **Instalabilidad**: Despliegue estandarizado vía archivo WAR.
