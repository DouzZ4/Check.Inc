<?php
// controllers/GlucosaController.php

// Asegúrate que Glucosa y GlucosaFactory están disponibles
require_once '../models/GlucosaModel.php'; // Contiene Glucosa y GlucosaFactory

class GlucosaController {
    private $conn;

    public function __construct($db) {
        $this->conn = $db; // Conexión a la base de datos (PDO)
        $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); // Asegurar manejo de errores
    }

    // --- Método crearRegistro (ya existente, con validación por setters) ---
    public function crearRegistro($data): array {
        // ... (Código como te lo mostré antes, usando setters y try-catch) ...
        // ... (Asegúrate que está aquí) ...
        try {
            // Validación básica de existencia
            if (empty($data['idUsuario']) || !isset($data['nivelGlucosa']) || empty($data['fechaHora'])) {
                 // Usamos !isset para nivelGlucosa porque 0 podría ser un valor válido pero empty() lo consideraría vacío.
                return ['status' => 'error', 'message' => 'idUsuario, nivelGlucosa y fechaHora son obligatorios.'];
            }

            // Crear objeto Glucosa usando la fábrica y los setters para validar
            // La validación ahora ocurre DENTRO de los setters de la clase Glucosa
            $glucosa = GlucosaFactory::crearGlucosa(null, null, null); // Crear objeto vacío primero
            $glucosa->setIdUsuario($data['idUsuario']); // Valida ID Usuario
            $glucosa->setNivelGlucosa($data['nivelGlucosa']); // Valida Nivel Glucosa
            $glucosa->setFechaHora($data['fechaHora']); // Valida Fecha Hora

            // Si llegamos aquí, la validación en los setters fue exitosa

            $sql = "INSERT INTO glucosa (idUsuario, nivelGlucosa, fechaHora) VALUES (:idUsuario, :nivelGlucosa, :fechaHora)";
            $stmt = $this->conn->prepare($sql);

            // Usamos bindValue que es generalmente más flexible que bindParam
            $stmt->bindValue(':idUsuario', $glucosa->getIdUsuario());
            $stmt->bindValue(':nivelGlucosa', $glucosa->getNivelGlucosa());
            $stmt->bindValue(':fechaHora', $glucosa->getFechaHora()); // Asume que la BD acepta el formato de string validado

            if ($stmt->execute()) {
                // Podrías devolver el ID insertado si lo necesitas: $this->conn->lastInsertId();
                return ['status' => 'success', 'message' => 'Registro creado exitosamente.'];
            } else {
                // Esto es menos probable con PDO::ERRMODE_EXCEPTION activado, pero por si acaso
                return ['status' => 'error', 'message' => 'Error al crear el registro en la base de datos.', 'details' => $stmt->errorInfo()];
            }
        } catch (PDOException $e) {
             // Captura excepciones de base de datos (ej. clave duplicada, etc.)
             return ['status' => 'error', 'message' => 'Error de base de datos al crear: ' . $e->getMessage()];
        } catch (Exception $e) {
            // Captura excepciones de validación de los setters
            return ['status' => 'error', 'message' => $e->getMessage()];
        }

    }

    // --- Método obtenerRegistros (ya existente) ---
    public function obtenerRegistros($idUsuario): array {
        // ... (Tu código existente para obtener todos los registros) ...
         try {
            $sql = "SELECT idGlucosa, idUsuario, nivelGlucosa, fechaHora FROM glucosa WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC";
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT); // Especificar tipo es buena práctica
            $stmt->execute();
            $resultados = $stmt->fetchAll(PDO::FETCH_ASSOC);
             return ['status' => 'success', 'data' => $resultados];
        } catch (PDOException $e) {
            return ['status' => 'error', 'message' => 'Error al obtener registros: ' . $e->getMessage()];
        }
    }

    // --- NUEVO: Método para obtener un ÚNICO Registro por ID ---
    /**
     * Obtiene un registro de glucosa específico por su ID.
     * @param int $idGlucosa El ID del registro a obtener.
     * @return array Estado de la operación y datos del registro si se encuentra.
     */
    public function obtenerRegistroPorId(int $idGlucosa): array {
         try {
            $sql = "SELECT idGlucosa, idUsuario, nivelGlucosa, fechaHora FROM glucosa WHERE idGlucosa = :idGlucosa";
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idGlucosa', $idGlucosa, PDO::PARAM_INT);
            $stmt->execute();
            $registro = $stmt->fetch(PDO::FETCH_ASSOC); // Fetch solo uno

            if ($registro) {
                 return ['status' => 'success', 'data' => $registro];
            } else {
                 return ['status' => 'error', 'message' => 'Registro no encontrado.'];
            }
        } catch (PDOException $e) {
            return ['status' => 'error', 'message' => 'Error de base de datos al obtener registro: ' . $e->getMessage()];
        }
    }


    // --- Método actualizarRegistro (código proporcionado anteriormente) ---
    /**
     * Actualiza un registro de glucosa existente.
     * @param int $idGlucosa El ID del registro a actualizar.
     * @param array $data Array asociativo con los datos a actualizar.
     * @return array Estado de la operación.
     */
    public function actualizarRegistro(int $idGlucosa, array $data): array {
        // ... (Código como te lo mostré antes, con validación, try-catch, UPDATE) ...
        // ... (Asegúrate que está aquí) ...
         try {
            // 1. Validar que haya datos para actualizar
            if (empty($data)) {
                return ['status' => 'error', 'message' => 'No se proporcionaron datos para actualizar.'];
            }

            // 2. Validar los datos usando los setters de la entidad Glucosa
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

            // 3. Verificar si hay campos válidos para actualizar
            if (empty($camposParaActualizar)) {
                 return ['status' => 'error', 'message' => 'No hay campos válidos para actualizar o los campos proporcionados no son modificables.'];
            }

            // 4. Construir la consulta SQL dinámicamente
            $setParts = [];
            $params = [':idGlucosa' => $idGlucosa]; // Parámetro para el WHERE

            foreach ($camposParaActualizar as $columna => $valor) {
                 $placeholder = ':' . $columna;
                 $setParts[] = "`" . $columna . "` = " . $placeholder;
                 $params[$placeholder] = $valor;
            }

            $sql = "UPDATE glucosa SET " . implode(', ', $setParts) . " WHERE idGlucosa = :idGlucosa";

            // 5. Preparar y ejecutar la consulta
            $stmt = $this->conn->prepare($sql);

            // Bind todos los parámetros
             // Bind :idGlucosa como INT
            $stmt->bindValue(':idGlucosa', $idGlucosa, PDO::PARAM_INT);
             // Bind los otros parámetros (principalmente STR, excepto nivelGlucosa podría ser INT/DECIMAL)
             foreach ($camposParaActualizar as $columna => $valor) {
                 $placeholder = ':' . $columna;
                  // Asume STR por defecto, ajusta si es necesario (ej. nivelGlucosa)
                 $tipo = PDO::PARAM_STR;
                  if ($columna === 'nivelGlucosa' && (is_int($valor) || is_float($valor))) {
                      // Podrías usar PDO::PARAM_INT o dejar que PDO lo maneje si es numérico
                      // PDO::PARAM_STR suele ser seguro para números también en muchos casos
                  }
                 $stmt->bindValue($placeholder, $valor, $tipo);
             }


            if ($stmt->execute()) {
                if ($stmt->rowCount() > 0) {
                    return ['status' => 'success', 'message' => 'Registro actualizado exitosamente.'];
                } else {
                    // Verificar si el registro existe para dar un mensaje más preciso
                    $checkStmt = $this->conn->prepare("SELECT COUNT(*) FROM glucosa WHERE idGlucosa = :idGlucosa");
                    $checkStmt->bindParam(':idGlucosa', $idGlucosa, PDO::PARAM_INT);
                    $checkStmt->execute();
                    if ($checkStmt->fetchColumn() > 0) {
                        return ['status' => 'info', 'message' => 'No se realizaron cambios (los datos eran idénticos).'];
                    } else {
                         return ['status' => 'info', 'message' => 'No se realizaron cambios (el registro no existe).']; // O podría ser 'error' 404
                    }
                }
            } else {
                 // Ya no debería ocurrir con ERRMODE_EXCEPTION
                 return ['status' => 'error', 'message' => 'Error desconocido al actualizar.', 'details' => $stmt->errorInfo()];
            }

        } catch (PDOException $e) {
            return ['status' => 'error', 'message' => 'Error de base de datos al actualizar: ' . $e->getMessage()];
        } catch (Exception $e) {
            // Captura excepciones de validación
           return ['status' => 'error', 'message' => $e->getMessage()];
       }
    }

    // --- Método eliminarRegistro (código proporcionado anteriormente) ---
    /**
     * Elimina un registro de glucosa por su ID.
     * @param int $idGlucosa El ID del registro a eliminar.
     * @return array Estado de la operación.
     */
    public function eliminarRegistro(int $idGlucosa): array {
        // ... (Código como te lo mostré antes, con DELETE y rowCount) ...
        // ... (Asegúrate que está aquí) ...
         try {
            $sql = "DELETE FROM glucosa WHERE idGlucosa = :idGlucosa";
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idGlucosa', $idGlucosa, PDO::PARAM_INT);

            if ($stmt->execute()) {
                if ($stmt->rowCount() > 0) {
                    return ['status' => 'success', 'message' => 'Registro eliminado exitosamente.'];
                } else {
                    // ID no encontrado, informamos
                    return ['status' => 'info', 'message' => 'No se eliminó ningún registro (el ID proporcionado no existe).'];
                }
            } else {
                 // Ya no debería ocurrir con ERRMODE_EXCEPTION
                 return ['status' => 'error', 'message' => 'Error desconocido al eliminar.', 'details' => $stmt->errorInfo()];
            }
        } catch (PDOException $e) {
             return ['status' => 'error', 'message' => 'Error de base de datos al eliminar: ' . $e->getMessage()];
        } catch (Exception $e) {
            // Otras posibles excepciones
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

} // Fin de la clase GlucosaController
?>