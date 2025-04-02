<?php
// controllers/citaController.php

require_once __DIR__ . '/../models/CitasModel.php';
require_once __DIR__ . '/../models/Usuario.php';

class CitaController {
    private $conn;

    public function __construct($db) {
        $this->conn = $db; // Conexión a la base de datos
    }

    public function crearCita($data): array {
        try {
            // Validación de campos obligatorios
            if (empty($data['fecha']) || empty($data['hora']) || empty($data['motivo']) || empty($data['idUsuario'])) {
                return ['status' => 'error', 'message' => 'Todos los campos son obligatorios.'];
            }

            // Crear objeto Cita usando la fábrica
            $cita = CitasFactory::crearCita($data['fecha'], $data['hora'], $data['motivo'], $data['idUsuario']);

            $sql = "INSERT INTO cita (fecha, hora, motivo, idUsuario) VALUES (:fecha, :hora, :motivo, :idUsuario)";
            $stmt = $this->conn->prepare($sql);

            $stmt->bindParam(':fecha', $cita->getFecha());
            $stmt->bindParam(':hora', $cita->getHora());
            $stmt->bindParam(':motivo', $cita->getMotivo());
            $stmt->bindParam(':idUsuario', $cita->getIdUsuario());

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
            $sql = "SELECT * FROM cita WHERE idUsuario = :idUsuario ORDER BY fecha DESC";
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