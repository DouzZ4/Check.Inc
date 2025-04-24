<?php
// models/GlucosaModel.php

class Glucosa {
    private $idGlucosa;
    private $nivelGlucosa;
    private $fechaHora;
    private $idUsuario;

    public function __construct($idUsuario = null, $nivelGlucosa = null, $fechaHora = null) {
        $this->idUsuario = $idUsuario;
        $this->nivelGlucosa = $nivelGlucosa;
        $this->fechaHora = $fechaHora;
    }

    // Getters
    public function getIdGlucosa() {
        return $this->idGlucosa;
    }

    public function getIdUsuario() {
        return $this->idUsuario;
    }

    public function getNivelGlucosa() {
        return $this->nivelGlucosa;
    }

    public function getFechaHora() {
        return $this->fechaHora;
    }

    // Setters
    public function setIdUsuario($idUsuario) {
        if (is_numeric($idUsuario) && $idUsuario > 0) {
            $this->idUsuario = $idUsuario;
        } else {
            throw new Exception("❌ El ID del usuario no es válido.");
        }
    }

    public function setNivelGlucosa($nivelGlucosa) {
        if ($nivelGlucosa > 0) {
            $this->nivelGlucosa = $nivelGlucosa;
        } else {
            throw new Exception("❌ El nivel de glucosa debe ser un valor positivo.");
        }
    }

    public function setFechaHora($fechaHora) {
        if (strtotime($fechaHora) !== false) {
            $this->fechaHora = $fechaHora;
        } else {
            throw new Exception("❌ La fecha y hora no tienen un formato válido.");
        }
    }
}

class GlucosaFactory {
    public static function crearGlucosa($idUsuario, $nivelGlucosa, $fechaHora) {
        return new Glucosa($idUsuario, $nivelGlucosa, $fechaHora);
    }
}
/**
 * Clase Modelo para manejar las operaciones de base de datos
 * relacionadas con los registros de glucosa.
 */
class GlucosaModel {
    private $db; // Conexión PDO

    /**
     * Constructor que recibe la conexión a la base de datos.
     * @param PDO $db Objeto de conexión PDO.
     */
    public function __construct(PDO $db) {
        $this->db = $db;
        // Asegurar que PDO lance excepciones en errores
        $this->db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }

    /**
     * Guarda un nuevo registro de glucosa en la base de datos.
     * @param Glucosa $glucosa Objeto entidad Glucosa con los datos a guardar.
     * @return int|false El ID del registro insertado o false en caso de error.
     */
    public function save(Glucosa $glucosa): int|false {
        $sql = "INSERT INTO glucosa (idUsuario, nivelGlucosa, fechaHora) VALUES (:idUsuario, :nivelGlucosa, :fechaHora)";
        try {
            $stmt = $this->db->prepare($sql);
            // Usamos bindValue directamente con los getters de la entidad
            $stmt->bindValue(':idUsuario', $glucosa->getIdUsuario(), PDO::PARAM_INT);
            $stmt->bindValue(':nivelGlucosa', $glucosa->getNivelGlucosa()); // PDO suele manejar bien números como string
            $stmt->bindValue(':fechaHora', $glucosa->getFechaHora()); // Asume formato de fecha compatible con BD

            if ($stmt->execute()) {
                return (int)$this->db->lastInsertId(); // Devuelve el nuevo ID
            } else {
                return false; // Error en ejecución (menos probable con excepciones activadas)
            }
        } catch (PDOException $e) {
            // Aquí podrías loguear el error $e->getMessage()
            error_log("Error en GlucosaModel::save: " . $e->getMessage());
            return false; // Indica fallo
        }
    }

    /**
     * Busca todos los registros de glucosa para un usuario específico.
     * @param int $idUsuario ID del usuario.
     * @return array Array de registros (como arrays asociativos) o array vacío si no hay.
     */
    public function findByUsuario(int $idUsuario): array {
        $sql = "SELECT idGlucosa, idUsuario, nivelGlucosa, fechaHora FROM glucosa WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC";
        try {
            $stmt = $this->db->prepare($sql);
            $stmt->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
            $stmt->execute();
            // Devolvemos arrays asociativos como esperaba el frontend actual
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            error_log("Error en GlucosaModel::findByUsuario: " . $e->getMessage());
            return []; // Devuelve array vacío en caso de error
        }
    }

    /**
     * Busca un registro de glucosa específico por su ID.
     * @param int $idGlucosa ID del registro.
     * @return array|null Array asociativo del registro o null si no se encuentra/error.
     */
    public function findById(int $idGlucosa): ?array {
         // Cambiado para devolver array asociativo para consistencia con findByUsuario
         // y simplificar el controller actual. Podría devolver ?Glucosa si prefieres.
        $sql = "SELECT idGlucosa, idUsuario, nivelGlucosa, fechaHora FROM glucosa WHERE idGlucosa = :idGlucosa";
        try {
            $stmt = $this->db->prepare($sql);
            $stmt->bindParam(':idGlucosa', $idGlucosa, PDO::PARAM_INT);
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            return $result ?: null; // Devuelve el array o null si fetch devuelve false
        } catch (PDOException $e) {
             error_log("Error en GlucosaModel::findById: " . $e->getMessage());
             return null;
        }
    }

    /**
     * Actualiza un registro de glucosa existente.
     * @param int $idGlucosa ID del registro a actualizar.
     * @param array $data Array asociativo con los datos validados a actualizar (columna => valor).
     * @return bool True si se actualizó al menos una fila, False en caso contrario o error.
     */
    public function update(int $idGlucosa, array $data): bool {
        if (empty($data)) {
            return false; // Nada que actualizar
        }

        $setParts = [];
        $params = [':idGlucosa' => $idGlucosa];

        foreach ($data as $columna => $valor) {
            // Asume que $data ya viene validado desde el Controller
            $placeholder = ':' . $columna;
            $setParts[] = "`" . $columna . "` = " . $placeholder;
            $params[$placeholder] = $valor;
        }

        $sql = "UPDATE glucosa SET " . implode(', ', $setParts) . " WHERE idGlucosa = :idGlucosa";

        try {
            $stmt = $this->db->prepare($sql);
            // Bind todos los parámetros
            foreach ($params as $placeholder => $valor) {
                $tipo = ($placeholder === ':idGlucosa') ? PDO::PARAM_INT : PDO::PARAM_STR;
                 // Podrías añadir más lógica de tipos si es necesario
                $stmt->bindValue($placeholder, $valor, $tipo);
            }

            $stmt->execute();
            return $stmt->rowCount() > 0; // Devuelve true si se afectaron filas

        } catch (PDOException $e) {
            error_log("Error en GlucosaModel::update: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de glucosa por su ID.
     * @param int $idGlucosa ID del registro a eliminar.
     * @return bool True si se eliminó al menos una fila, False en caso contrario o error.
     */
    public function delete(int $idGlucosa): bool {
        $sql = "DELETE FROM glucosa WHERE idGlucosa = :idGlucosa";
        try {
            $stmt = $this->db->prepare($sql);
            $stmt->bindParam(':idGlucosa', $idGlucosa, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->rowCount() > 0; // Devuelve true si se afectaron filas
        } catch (PDOException $e) {
             error_log("Error en GlucosaModel::delete: " . $e->getMessage());
             return false;
        }
    }
    // Dentro de la clase GlucosaModel en models/GlucosaModel.php

/**
 * Cuenta el número total de registros de glucosa.
 * @return int El número total de registros.
 */
public function contarTotalRegistros(): int {
    try {
        $sql = "SELECT COUNT(*) FROM glucosa";
        $stmt = $this->db->query($sql); // Asumiendo que tu conexión se llama $db aquí
        return (int)$stmt->fetchColumn();
    } catch (PDOException $e) {
        error_log("Error en GlucosaModel::contarTotalRegistros: " . $e->getMessage());
        return 0;
    }
}

// ... resto de métodos existentes ...
}
?>