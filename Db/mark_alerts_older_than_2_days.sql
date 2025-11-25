-- Script: marcar alertas como vistas si fueron creadas hace más de 2 días
-- Ejecútese en la base de datos 'checks'

UPDATE alerta
SET visto = 1
WHERE fechaHora < (NOW() - INTERVAL 2 DAY)
  AND (visto = 0 OR visto IS NULL);

-- Para verificar el número afectado:
SELECT ROW_COUNT() AS filas_afectadas;
