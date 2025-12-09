-- SCRIPT DE POBLADO DE DATOS MASIVOS PARA DEMO CHECK.INC (DIC 2025)
-- Autor: Antigravity
-- Objetivo: Poblar historial de glucosa, citas, anomalías y medicamentos para el último mes.

-- =============================================
-- 1. LIMPIEZA DE DATOS PREVIOS (Opcional)
-- =============================================
-- DELETE FROM glucosa WHERE idUsuario IN (1, 15) AND fechaHora >= '2025-11-01 00:00:00';
-- DELETE FROM cita WHERE idUsuario IN (1, 15) AND fecha >= '2025-11-01';
-- DELETE FROM anomalia WHERE idUsuario IN (1, 15) AND fechaHora >= '2025-11-01 00:00:00';
-- DELETE FROM medicamento WHERE idUsuario IN (1, 15);

-- =============================================
-- 2. INSERCIÓN MASIVA DE GLUCOSA (Usuario ID 1 - Yeikol 2)
-- Generando datos desde el 1 de Noviembre al 10 de Diciembre
-- =============================================

-- Noviembre 2025
INSERT INTO glucosa (nivelGlucosa, fechaHora, idUsuario, momentoDia) VALUES 
(95.5, '2025-11-01 07:30:00', 1, 'En Ayuno'), (135.2, '2025-11-01 13:30:00', 1, 'Después de Almuerzo'), (110.0, '2025-11-01 20:30:00', 1, 'Después de Cena'),
(92.0, '2025-11-02 08:00:00', 1, 'En Ayuno'), (140.5, '2025-11-02 14:00:00', 1, 'Después de Almuerzo'), (115.3, '2025-11-02 21:00:00', 1, 'Después de Cena'),
(88.5, '2025-11-03 07:45:00', 1, 'En Ayuno'), (128.0, '2025-11-03 13:15:00', 1, 'Después de Almuerzo'), (108.0, '2025-11-03 20:15:00', 1, 'Después de Cena'),
(90.0, '2025-11-04 07:30:00', 1, 'En Ayuno'), (132.0, '2025-11-04 13:30:00', 1, 'Después de Almuerzo'), (112.5, '2025-11-04 20:30:00', 1, 'Después de Cena'),
(85.0, '2025-11-05 08:00:00', 1, 'En Ayuno'), (125.5, '2025-11-05 14:00:00', 1, 'Después de Almuerzo'), (105.0, '2025-11-05 21:00:00', 1, 'Después de Cena'),
(98.0, '2025-11-06 07:30:00', 1, 'En Ayuno'), (145.0, '2025-11-06 13:30:00', 1, 'Después de Almuerzo'), (120.0, '2025-11-06 20:30:00', 1, 'Después de Cena'),
(94.5, '2025-11-07 07:45:00', 1, 'En Ayuno'), (138.0, '2025-11-07 13:15:00', 1, 'Después de Almuerzo'), (118.0, '2025-11-07 20:15:00', 1, 'Después de Cena'),
(175.0, '2025-11-08 20:00:00', 1, 'Después de Cena'), -- Pico Alto (Cena pesada)
(91.0, '2025-11-09 08:00:00', 1, 'En Ayuno'), (130.0, '2025-11-09 14:00:00', 1, 'Después de Almuerzo'), (110.0, '2025-11-09 21:00:00', 1, 'Después de Cena'),
(89.5, '2025-11-10 07:30:00', 1, 'En Ayuno'), (127.5, '2025-11-10 13:30:00', 1, 'Después de Almuerzo'), (107.5, '2025-11-10 20:30:00', 1, 'Después de Cena'),
(96.0, '2025-11-11 07:45:00', 1, 'En Ayuno'), (142.0, '2025-11-11 13:15:00', 1, 'Después de Almuerzo'), (116.0, '2025-11-11 20:15:00', 1, 'Después de Cena'),
(65.0, '2025-11-12 11:00:00', 1, 'Antes de Almuerzo'), -- Hipoglucemia leve
(105.0, '2025-11-13 08:00:00', 1, 'En Ayuno'), (135.0, '2025-11-13 14:00:00', 1, 'Después de Almuerzo'), (115.0, '2025-11-13 21:00:00', 1, 'Después de Cena'),
(93.0, '2025-11-14 07:30:00', 1, 'En Ayuno'), (129.0, '2025-11-14 13:30:00', 1, 'Después de Almuerzo'), (109.0, '2025-11-14 20:30:00', 1, 'Después de Cena'),
(87.5, '2025-11-15 07:45:00', 1, 'En Ayuno'), (126.5, '2025-11-15 13:15:00', 1, 'Después de Almuerzo'), (106.5, '2025-11-15 20:15:00', 1, 'Después de Cena'),
(102.0, '2025-11-16 08:00:00', 1, 'En Ayuno'), (138.0, '2025-11-16 14:00:00', 1, 'Después de Almuerzo'), (118.0, '2025-11-16 21:00:00', 1, 'Después de Cena'),
(99.0, '2025-11-17 07:30:00', 1, 'En Ayuno'), (148.0, '2025-11-17 13:30:00', 1, 'Después de Almuerzo'), (122.0, '2025-11-17 20:30:00', 1, 'Después de Cena'),
(90.5, '2025-11-18 07:45:00', 1, 'En Ayuno'), (131.0, '2025-11-18 13:15:00', 1, 'Después de Almuerzo'), (111.0, '2025-11-18 20:15:00', 1, 'Después de Cena'),
(86.0, '2025-11-19 08:00:00', 1, 'En Ayuno'), (124.0, '2025-11-19 14:00:00', 1, 'Después de Almuerzo'), (104.0, '2025-11-19 21:00:00', 1, 'Después de Cena'),
(185.0, '2025-11-20 15:00:00', 1, 'Después de Almuerzo'), -- Pico Alto (Cumpleaños)
(97.0, '2025-11-21 07:30:00', 1, 'En Ayuno'), (140.0, '2025-11-21 13:30:00', 1, 'Después de Almuerzo'), (120.0, '2025-11-21 20:30:00', 1, 'Después de Cena'),
(92.5, '2025-11-22 07:45:00', 1, 'En Ayuno'), (133.0, '2025-11-22 13:15:00', 1, 'Después de Almuerzo'), (113.0, '2025-11-22 20:15:00', 1, 'Después de Cena'),
(89.0, '2025-11-23 08:00:00', 1, 'En Ayuno'), (128.5, '2025-11-23 14:00:00', 1, 'Después de Almuerzo'), (108.5, '2025-11-23 21:00:00', 1, 'Después de Cena'),
(100.0, '2025-11-24 07:30:00', 1, 'En Ayuno'), (136.0, '2025-11-24 13:30:00', 1, 'Después de Almuerzo'), (116.0, '2025-11-24 20:30:00', 1, 'Después de Cena'),
(94.0, '2025-11-25 07:45:00', 1, 'En Ayuno'), (139.0, '2025-11-25 13:15:00', 1, 'Después de Almuerzo'), (119.0, '2025-11-25 20:15:00', 1, 'Después de Cena'),
(88.0, '2025-11-26 08:00:00', 1, 'En Ayuno'), (125.0, '2025-11-26 14:00:00', 1, 'Después de Almuerzo'), (105.0, '2025-11-26 21:00:00', 1, 'Después de Cena'),
(103.0, '2025-11-27 07:30:00', 1, 'En Ayuno'), (143.0, '2025-11-27 13:30:00', 1, 'Después de Almuerzo'), (123.0, '2025-11-27 20:30:00', 1, 'Después de Cena'),
(91.5, '2025-11-28 07:45:00', 1, 'En Ayuno'), (130.5, '2025-11-28 13:15:00', 1, 'Después de Almuerzo'), (110.5, '2025-11-28 20:15:00', 1, 'Después de Cena'),
(85.5, '2025-11-29 08:00:00', 1, 'En Ayuno'), (122.0, '2025-11-29 14:00:00', 1, 'Después de Almuerzo'), (102.0, '2025-11-29 21:00:00', 1, 'Después de Cena'),
(96.5, '2025-11-30 07:30:00', 1, 'En Ayuno'), (137.0, '2025-11-30 13:30:00', 1, 'Después de Almuerzo'), (117.0, '2025-11-30 20:30:00', 1, 'Después de Cena');

-- Diciembre 2025 (Hasta la fecha de la demo)
INSERT INTO glucosa (nivelGlucosa, fechaHora, idUsuario, momentoDia) VALUES 
(93.5, '2025-12-01 07:45:00', 1, 'En Ayuno'), (134.0, '2025-12-01 13:15:00', 1, 'Después de Almuerzo'), (114.0, '2025-12-01 20:15:00', 1, 'Después de Cena'),
(87.0, '2025-12-02 08:00:00', 1, 'En Ayuno'), (126.0, '2025-12-02 14:00:00', 1, 'Después de Almuerzo'), (106.0, '2025-12-02 21:00:00', 1, 'Después de Cena'),
(101.0, '2025-12-03 07:30:00', 1, 'En Ayuno'), (141.0, '2025-12-03 13:30:00', 1, 'Después de Almuerzo'), (121.0, '2025-12-03 20:30:00', 1, 'Después de Cena'),
(92.0, '2025-12-04 07:45:00', 1, 'En Ayuno'), (131.5, '2025-12-04 13:15:00', 1, 'Después de Almuerzo'), (111.5, '2025-12-04 20:15:00', 1, 'Después de Cena'),
(89.0, '2025-12-05 08:00:00', 1, 'En Ayuno'), (128.0, '2025-12-05 14:00:00', 1, 'Después de Almuerzo'), (108.0, '2025-12-05 21:00:00', 1, 'Después de Cena'),
(97.5, '2025-12-06 07:30:00', 1, 'En Ayuno'), (136.5, '2025-12-06 13:30:00', 1, 'Después de Almuerzo'), (116.5, '2025-12-06 20:30:00', 1, 'Después de Cena'),
(90.0, '2025-12-07 07:45:00', 1, 'En Ayuno'), (129.5, '2025-12-07 13:15:00', 1, 'Después de Almuerzo'), (109.5, '2025-12-07 20:15:00', 1, 'Después de Cena'),
(100.0, '2025-12-08 23:32:00', 1, 'Después de Cena'); -- Ultima lectura real

-- =============================================
-- 3. INSERCIÓN DE CITAS (Usuario ID 1 y 15)
-- =============================================
INSERT INTO cita (fecha, hora, motivo, idUsuario, estado) VALUES
('2025-12-15', '09:00:00', 'Control Nutricional', 1, 'Pendiente'),
('2025-12-20', '15:30:00', 'Chequeo General Dr. Gomez', 1, 'Pendiente'),
('2026-01-10', '10:00:00', 'Examen de Fondo de Ojo', 1, 'Pendiente'),
('2025-11-28', '11:00:00', 'Consulta Diabetes Trimestral', 1, 'Completada'),
('2025-12-18', '08:30:00', 'Exámenes de Laboratorio', 15, 'Pendiente'),
('2025-12-22', '14:00:00', 'Control Podología', 15, 'Pendiente');

-- =============================================
-- 4. INSERCIÓN DE ANOMALIAS (Para probar alertas y tabla)
-- =============================================
INSERT INTO anomalia (descripcion, fechaHora, sintomas, idUsuario, gravedad, resuelto) VALUES
('Mareo leve tras ejercicio', '2025-11-12 11:30:00', 'Sensación de desvanecimiento, sudoración.', 1, 'leve', 1),
('Pico de glucosa post-cumpleaños', '2025-11-20 16:00:00', 'Sed excesiva, visión borrosa momentánea.', 1, 'grave', 1),
('Hipoglucemia nocturna', '2025-12-05 03:00:00', 'Despertar con temblor y hambre.', 1, 'moderada', 0);

-- =============================================
-- 5. INSERCIÓN DE ALERTAS (Para panel administrador)
-- =============================================
INSERT INTO alerta (tipo, contenido, fechaHora, idUsuario, visto) VALUES
('ALERTA_GLUCOSA', 'Nivel Crítico Alto detectado: 185 mg/dL. Se envió notificación al contacto de emergencia.', '2025-11-20 15:05:00', 1, 1),
('ALERTA_ANOMALIA', 'Anomalía Grave Reportada: Pico de glucosa. El usuario reportó síntomas severos.', '2025-11-20 16:05:00', 1, 1),
('ALERTA_GLUCOSA', 'Hipoglucemia detectada: 65 mg/dL. Alerta preventiva enviada.', '2025-11-12 11:05:00', 1, 1);

-- =============================================
-- 6. INSERCIÓN DE MEDICAMENTOS (Para completar la tarjeta del Dashboard)
-- =============================================
INSERT INTO medicamento (nombre, dosis, frecuencia, fechaInicio, fechaFin, idUsuario, observaciones) VALUES
('Insulina Glargina (Lantus)', '15 UI', '1 vez al día (Noche)', '2025-06-01', '2026-06-01', 1, 'Aplicar en zona abdominal'),
('Metformina', '850 mg', 'Con cada comida', '2025-01-10', '2026-01-10', 1, 'No suspender sin orden médica'),
('Losartán', '50 mg', 'Cada 12 horas', '2025-08-15', '2026-08-15', 1, 'Para control de presión arterial'),
('Insulina Lispro (Humalog)', '5 UI', 'Antes de comidas principales', '2025-11-01', '2026-05-01', 1, 'Corrección rápida según esquema'),
('Atorvastatina', '20 mg', '1 vez al día (Noche)', '2025-02-20', '2026-02-20', 15, 'Preventivo cardiovascular'),
('Metformina XR', '1000 mg', 'Cena', '2025-03-01', '2026-03-01', 15, 'Liberación prolongada');
