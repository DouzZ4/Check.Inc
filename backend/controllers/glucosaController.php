<?php
// controllers/GlucosaController.php

// Requerir el archivo que contiene GlucosaModel, Glucosa, GlucosaFactory
require_once '../models/GlucosaModel.php';

class GlucosaController {
    // Cambiamos la dependencia: ya no es la conexión directa, sino el Modelo
    private $glucosaModel;

    /**
     * Constructor que recibe la instancia del Modelo.
     * @param GlucosaModel $model Instancia de GlucosaModel.
     */
    public function __construct(GlucosaModel $model) {
        $this->glucosaModel = $model;
    }

    /**
     * Maneja la creación de un nuevo registro de glucosa.
     * @param array $data Datos recibidos (idUsuario, nivelGlucosa, fechaHora).
     * @return array Respuesta estándar ['status' => ..., 'message' => ...].
     */
    public function crearRegistro($data): array {
        try {
            // 1. Validación de existencia de datos (como antes)
            if (empty($data['idUsuario']) || !isset($data['nivelGlucosa']) || empty($data['fechaHora'])) {
                return ['status' => 'error', 'message' => 'idUsuario, nivelGlucosa y fechaHora son obligatorios.'];
            }

            // 2. Crear y validar la entidad usando Setters (como antes)
            $glucosa = new Glucosa(); // Usar Factory es opcional aquí si validamos con setters
            $glucosa->setIdUsuario($data['idUsuario']);
            $glucosa->setNivelGlucosa($data['nivelGlucosa']);
            $glucosa->setFechaHora($data['fechaHora']);

            // 3. Llamar al Modelo para guardar
            $nuevoId = $this->glucosaModel->save($glucosa);

            // 4. Comprobar resultado del Modelo
            if ($nuevoId !== false) {
                return ['status' => 'success', 'message' => 'Registro creado exitosamente.', 'id' => $nuevoId];
            } else {
                return ['status' => 'error', 'message' => 'Error del servidor al intentar guardar el registro.'];
            }
        } catch (Exception $e) {
            // Captura excepciones de validación de los setters
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    /**
     * Obtiene todos los registros para un usuario.
     * @param int $idUsuario ID del usuario.
     * @return array Respuesta con los datos o error.
     */
    public function obtenerRegistros($idUsuario): array {
        try {
            // Llama directamente al modelo
            $registros = $this->glucosaModel->findByUsuario($idUsuario);
            // El modelo devuelve el array de resultados o [] en error/vacío
            return ['status' => 'success', 'data' => $registros];
        } catch (Exception $e) {
             // Captura por si acaso, aunque el modelo debería manejar PDOExceptions
             error_log("Error inesperado en GlucosaController::obtenerRegistros: " . $e->getMessage());
             return ['status' => 'error', 'message' => 'Error inesperado del servidor al obtener registros.'];
        }
    }

    /**
     * Obtiene un registro específico por ID.
     * @param int $idGlucosa ID del registro.
     * @return array Respuesta con el dato o error.
     */
    public function obtenerRegistroPorId(int $idGlucosa): array {
        try {
            $registro = $this->glucosaModel->findById($idGlucosa);
            if ($registro !== null) {
                 return ['status' => 'success', 'data' => $registro];
            } else {
                 // El modelo devolvió null (no encontrado o error DB)
                 return ['status' => 'error', 'message' => 'Registro no encontrado.'];
            }
        } catch (Exception $e) {
             error_log("Error inesperado en GlucosaController::obtenerRegistroPorId: " . $e->getMessage());
             return ['status' => 'error', 'message' => 'Error inesperado del servidor al obtener el registro.'];
        }
    }

    /**
     * Actualiza un registro existente.
     * @param int $idGlucosa ID del registro a actualizar.
     * @param array $data Nuevos datos (nivelGlucosa, fechaHora).
     * @return array Respuesta estándar.
     */
    public function actualizarRegistro(int $idGlucosa, array $data): array {
        try {
            // 1. Validación de existencia de datos (como antes)
            if (empty($data)) {
                return ['status' => 'error', 'message' => 'No se proporcionaron datos para actualizar.'];
            }

            // 2. Validación de los datos individuales usando Setters (como antes)
            $glucosaValidada = new Glucosa();
            $camposParaActualizar = [];
            if (isset($data['nivelGlucosa'])) {
                $glucosaValidada->setNivelGlucosa($data['nivelGlucosa']);
                $camposParaActualizar['nivelGlucosa'] = $glucosaValidada->getNivelGlucosa();
            }
            if (isset($data['fechaHora'])) {
                $glucosaValidada->setFechaHora($data['fechaHora']);
                $camposParaActualizar['fechaHora'] = $glucosaValidada->getFechaHora();
            }
            // Añadir más campos si son editables...

            if (empty($camposParaActualizar)) {
                 return ['status' => 'error', 'message' => 'No hay campos válidos para actualizar.'];
            }

            // 3. Llamar al Modelo para actualizar
            $actualizado = $this->glucosaModel->update($idGlucosa, $camposParaActualizar);

            // 4. Interpretar resultado del Modelo
            if ($actualizado) {
                return ['status' => 'success', 'message' => 'Registro actualizado exitosamente.'];
            } else {
                // Podría ser que no existía o que los datos eran iguales.
                // Para un mensaje más preciso, podríamos llamar a findById primero,
                // pero por ahora, mantenemos un mensaje genérico de fallo/sin cambios.
                // O el modelo podría devolver rowCount para distinguir.
                 // El modelo devuelve true solo si rowCount > 0
                 return ['status' => 'info', 'message' => 'No se realizaron cambios (registro no encontrado o datos idénticos).'];
            }

        } catch (Exception $e) {
            // Captura excepciones de validación de los setters
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    /**
     * Elimina un registro.
     * @param int $idGlucosa ID del registro a eliminar.
     * @return array Respuesta estándar.
     */
    public function eliminarRegistro(int $idGlucosa): array {
         try {
             // 1. Llamar al Modelo para eliminar
             $eliminado = $this->glucosaModel->delete($idGlucosa);

             // 2. Interpretar resultado
             if ($eliminado) {
                 return ['status' => 'success', 'message' => 'Registro eliminado exitosamente.'];
             } else {
                  // El modelo devuelve false si rowCount es 0 (no existía) o si hay error PDO
                  // El modelo ya loguea el error PDO, aquí indicamos que no se encontró.
                 return ['status' => 'info', 'message' => 'No se eliminó ningún registro (el ID proporcionado no existe).'];
             }
         } catch (Exception $e) {
              error_log("Error inesperado en GlucosaController::eliminarRegistro: " . $e->getMessage());
              return ['status' => 'error', 'message' => 'Error inesperado del servidor al eliminar.'];
         }
    }
}
?>