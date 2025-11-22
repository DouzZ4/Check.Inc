-- =====================================================
-- TABLA: nivelesGlucosa
-- Descripción: Almacena los rangos normales de glucosa por tipo de diabetes
--              y personalizaciones por usuario
-- =====================================================

CREATE TABLE IF NOT EXISTS nivelesGlucosa (
    idNivelGlucosa INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Relación con usuario (NULL = valores por defecto del sistema)
    idUsuario INT UNIQUE,
    
    -- Tipo de diabetes (Tipo 1, Tipo 2, Gestacional)
    tipoDiabetes VARCHAR(50),
    
    -- Rangos normales (en mg/dL)
    nivelMinimo FLOAT NOT NULL DEFAULT 80.0,
    nivelMaximo FLOAT NOT NULL DEFAULT 130.0,
    
    -- Umbrales críticos para alertas
    nivelBajoCritico FLOAT NOT NULL DEFAULT 70.0,
    nivelAltoCritico FLOAT NOT NULL DEFAULT 250.0,
    
    -- Control: ¿está activo este perfil de niveles?
    activo BOOLEAN DEFAULT TRUE,
    
    -- Auditoría
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fechaActualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Clave foránea
    CONSTRAINT fk_nivelesGlucosa_usuario 
        FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- VALORES POR DEFECTO DEL SISTEMA
-- Basados en recomendaciones ADA (American Diabetes Association)
-- y OMS (Organización Mundial de la Salud)
-- =====================================================

-- Tipo 1 (ADA: objetivo 80-130 mg/dL preprandial)
INSERT INTO nivelesGlucosa (idUsuario, tipoDiabetes, nivelMinimo, nivelMaximo, nivelBajoCritico, nivelAltoCritico, activo)
VALUES (NULL, 'Tipo 1', 80.0, 130.0, 70.0, 250.0, TRUE);

-- Tipo 2 (ADA: objetivo 80-130 mg/dL preprandial, más tolerancia)
INSERT INTO nivelesGlucosa (idUsuario, tipoDiabetes, nivelMinimo, nivelMaximo, nivelBajoCritico, nivelAltoCritico, activo)
VALUES (NULL, 'Tipo 2', 80.0, 180.0, 70.0, 300.0, TRUE);

-- Gestacional (ADA: más estricto durante embarazo)
INSERT INTO nivelesGlucosa (idUsuario, tipoDiabetes, nivelMinimo, nivelMaximo, nivelBajoCritico, nivelAltoCritico, activo)
VALUES (NULL, 'Gestacional', 70.0, 120.0, 60.0, 200.0, TRUE);

-- Prediabetes / Sin diagnóstico (OMS: normal < 100 en ayuno)
INSERT INTO nivelesGlucosa (idUsuario, tipoDiabetes, nivelMinimo, nivelMaximo, nivelBajoCritico, nivelAltoCritico, activo)
VALUES (NULL, 'No especificado', 70.0, 100.0, 60.0, 180.0, TRUE);

-- =====================================================
-- ÍNDICES para optimización
-- =====================================================
CREATE INDEX idx_nivelesGlucosa_idUsuario ON nivelesGlucosa(idUsuario);
CREATE INDEX idx_nivelesGlucosa_tipoDiabetes ON nivelesGlucosa(tipoDiabetes);
CREATE INDEX idx_nivelesGlucosa_activo ON nivelesGlucosa(activo);

-- =====================================================
-- COMENTARIOS (documentación)
-- =====================================================
-- nivelMinimo: Límite inferior del rango normal
-- nivelMaximo: Límite superior del rango normal
-- nivelBajoCritico: Por debajo = HIPOGLUCEMIA (alerta roja)
-- nivelAltoCritico: Por encima = HIPERGLUCEMIA SEVERA (alerta roja)
-- 
-- INTERPRETACIÓN DE ESTADOS:
-- - nivel < nivelBajoCritico: "CRITICO_BAJO" (❌ Hipoglucemia)
-- - nivelBajoCritico ≤ nivel < nivelMinimo: "BAJO" (⚠️  Ligeramente bajo)
-- - nivelMinimo ≤ nivel ≤ nivelMaximo: "NORMAL" (✅)
-- - nivelMaximo < nivel ≤ nivelAltoCritico: "ALTO" (⚠️  Ligeramente alto)
-- - nivel > nivelAltoCritico: "CRITICO_ALTO" (❌ Hiperglucemia severa)
-- =====================================================
