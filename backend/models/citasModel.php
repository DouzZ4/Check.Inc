<?php

class Cita {
    private $idCita;
    private $fecha;
    private $hora;
    private $motivo;
    private $idUsuario;

    public function __construct($idUsuario = null, $fecha = null, $hora = null, $motivo = null) {
        $this->idUsuario = $idUsuario;
        $this->fecha = $fecha;
        $this->hora = $hora;
        $this->motivo = $motivo;
    }

    // Getters
    public function getIdCita() {
        return $this->idCita;
    }

    public function getFecha() {
        return $this->fecha;
    }

    public function getHora() {
        return $this->hora;
    }

    public function getMotivo() {
        return $this->motivo;
    }

    public function getIdUsuario() {
        return $this->idUsuario;
    }
    
    // Setters
    public function setIdUsuario($idUsuario) {
        if (is_numeric($idUsuario) && $idUsuario > 0) {
            $this->idUsuario = $idUsuario;
        } else {
            throw new Exception("❌ El ID del usuario no es válido.");
        }
    }

    public function setFecha($fecha) {
        if (strtotime($fecha) !== false) {
            $this->fecha = $fecha;
        } else {
            throw new Exception("❌ La fecha no tiene un formato válido.");
        }
    }

    public function setHora($hora) {
        if (preg_match('/^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/', $hora)) {
            $this->hora = $hora;
        } else {
            throw new Exception("❌ La hora no tiene un formato válido.");
        }
    }

    public function setMotivo($motivo) {
        if (!empty($motivo)) {
            $this->motivo = $motivo;
        } else {
            throw new Exception("❌ El motivo no puede estar vacío.");
        }
    }
}

class CitaFactory {
    public static function crearCita($idUsuario, $fecha, $hora, $motivo) {
        return new Cita($idUsuario, $fecha, $hora, $motivo);
    }
}

class CitaModel {
    private $db;
    
    
    public function __construct(PDO $db) {
        $this->db = $db;
        // Asegurar que PDO lance excepciones en errores
        $this->db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }

    //Guarda una nueva cita en la base de datos
    public function InsertarCita(Cita $cita) {
        $sql = "INSERT INTO cita (idUsuario, fecha, hora, motivo) VALUES (:idUsuario, :fecha, :hora, :motivo)";
        try {
            $stmt = $this->db->prepare($sql);
            $stmt->bindValue(':idUsuario', $cita->getIdUsuario(), PDO::PARAM_INT);
            $stmt->bindValue(':fecha', $cita->getFecha());
            $stmt->bindValue(':hora', $cita->getHora());
            $stmt->bindValue(':motivo', $cita->getMotivo());
            
            if ($stmt->execute()) {
                return $this->db->lastInsertId(); // Retorna el ID de la cita insertada
            } else {
                return false;
            }
        } catch (Exception $e) {
            throw new Exception("❌ Error al guardar la cita: " . $e->getMessage());
        }
    }

    //Obtiene todas las citas de un usuario
    public function ConsultarCita(int $idUsuario) {
        $sql = "SELECT * FROM cita WHERE idUsuario = :idUsuario";
        try {
            $stmt = $this->db->prepare($sql);
            $stmt->bindValue(':idUsuario', $idUsuario, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->fetchAll(PDO::FETCH_ASSOC); // Retorna todas las citas del usuario
        } catch (Exception $e) {
            throw new Exception("❌ Error al obtener las citas: " . $e->getMessage());
        }
    }

    //Actualiza una cita existente
    public function ActualizarCita(int $idCita, array $data) {
        if (empty($data)) {
            return false; // No hay datos para actualizar
        }

        $setParts = [];
        $params = [':idCita' => $idCita];

        foreach ($data as $columna => $valor) {
            $setParts[] = "`$columna` = :$columna";
            $params[":$columna"] = $valor;
        }

        $sql = "UPDATE cita SET " . implode(", ", $setParts) . " WHERE idCita = :idCita";
        try {
            $stmt = $this->db->prepare($sql);
            foreach ($params as $placeholder => $valor) {
                $stmt->bindValue($placeholder, $valor);
            }
            return $stmt->execute();
        } catch (PDOException $e) {
            throw new Exception("Error al actualizar la cita: " . $e->getMessage());
        }
    }

    //Elimina una cita por su ID

    public function EliminarCita(int $idCita) {
        $sql = "DELETE FROM cita WHERE idCita = :idCita";
        try {
            $stmt = $this->db->prepare($sql);
            $stmt->bindValue(':idCita', $idCita, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->rowCount() > 0; // Retorna true si se eliminó correctamente
        } catch (Exception $e) {
            throw new Exception("❌ Error al eliminar la cita: " . $e->getMessage());
        }
    }
    
    
}

?>