<?php

require_once '../models/CitasModel.php';

class CitaController {
    private $conn;

    public function __construct($db) {
        $this->conn = $db; // Conexión a la base de datos
    }

    public function crearCita($data): array {
        try {
            if (empty($data['fecha']) || empty($data['Hora']) || empty($data['motivo'])) {
                return ['status' => 'error', 'message' => 'Todos los campos son obligatorios.'];
            }

            // Crear objeto Cita usando la fábrica
            $cita = CitasFactory::crearCita(null, $data['fecha'], $data['Hora'], $data['motivo'], $_SESSION['idUsuario']);

            $sql = "INSERT INTO citas (fecha, hora, motivo, idUsuario) VALUES (:fecha, :Hora, :motivo, :idUsuario)";
            $stmt = $this->conn->prepare($sql);

            $stmt->bindParam(':fecha', $cita->getFecha());
            $stmt->bindParam(':Hora', $cita->getHora());
            $stmt->bindParam(':motivo', $cita->getMotivo());
            $stmt->bindParam(':idUsuario', $_SESSION['idUsuario']);

            if ($stmt->execute()) {
                return ['status' => 'success', 'message' => 'Cita creada exitosamente.'];
            } else {
                return ['status' => 'error', 'message' => 'Error al crear la cita.'];
            }
        } catch (Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    public function obtenerCitas($idUsuario): array {
        try {
            $sql = "SELECT * FROM citas WHERE idUsuario = :idUsuario ORDER BY fecha DESC";
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idUsuario', $idUsuario);
            $stmt->execute();
            return ['status' => 'success', 'data' => $stmt->fetchAll(PDO::FETCH_ASSOC)];
        } catch (PDOException $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }
}
?>