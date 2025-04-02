<?php
// controllers/GlucosaController.php

require_once '../models/GlucosaModel.php';

class GlucosaController {
    private $conn;

    public function __construct($db) {
        $this->conn = $db; // Conexión a la base de datos
    }

    public function crearRegistro($data): array {
        try {
            if (empty($data['idUsuario']) || empty($data['nivelGlucosa']) || empty($data['fechaHora'])) {
                return ['status' => 'error', 'message' => 'Todos los campos son obligatorios.'];
            }
            

            // Crear objeto Glucosa usando la fábrica
            $glucosa = GlucosaFactory::crearGlucosa($data['idUsuario'], $data['nivelGlucosa'], $data['fechaHora']);

            $sql = "INSERT INTO glucosa (idUsuario, nivelGlucosa, fechaHora) VALUES (:idUsuario, :nivelGlucosa, :fechaHora)";
                $stmt = $this->conn->prepare($sql);

                    $idUsuario = $glucosa->getIdUsuario(); // Asignar el valor a una variable
                    $nivelGlucosa = $glucosa->getNivelGlucosa(); // Asignar el valor a una variable
                    $fechaHora = $glucosa->getFechaHora(); // Asignar el valor a una variable

                    $stmt->bindParam(':idUsuario', $idUsuario);
                    $stmt->bindParam(':nivelGlucosa', $nivelGlucosa);
                    $stmt->bindParam(':fechaHora', $fechaHora);
            if ($stmt->execute()) {
                return ['status' => 'success', 'message' => 'Registro creado exitosamente.'];
            } else {
                return ['status' => 'error', 'message' => 'Error al crear el registro.'];
            }
        } catch (Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    public function obtenerRegistros($idUsuario): array {
        try {
            $sql = "SELECT * FROM glucosa WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC";
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