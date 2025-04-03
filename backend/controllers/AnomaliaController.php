<?php
// controller/AnomaliaController.php

require_once '../models/AnomaliaModel.php';

class AnomaliaController {
    private $conn;
    
    public function __construct($db) {
        $this->conn = $db; // Conexión a la base de datos
    }

    public function crearAnomalia($data): array {
        //manejo de errores
        try{
            if (empty($data['descripcion']) || empty($data['sintomas']) || empty($data['fechaHora']) || empty($data['idUsuario'])) {
                return ['status' => 'error', 'message' => 'Todos los campos son obligatorios.'];
            }


            //crear objeto Anomalia usando la fábrica
            $anomalia = AnomaliaFactory::crearAnomalia($data['idUsuario'], $data['descripcion'], $data['fechaHora'], $data['sintomas']);


            //preparación de la consulta SQL
            $sql = "INSERT INTO anomalia (idUsuario, descripcion, fechaHora, sintomas) VALUES (:idUsuario, :descripcion, :fechaHora, :sintomas)";
            $stmt = $this->conn->prepare($sql);

            //asignacion de parametros
            
            $idUsuario = $anomalia->getIdUsuario(); // Asignar el valor a una variable
            $descripcion = $anomalia->getDescripcion(); // Asignar el valor a una variable
            $fechaHora = $anomalia->getFechaHora(); // Asignar el valor a una variable
            $sintomas = $anomalia->getSintomas(); // Asignar el valor a una variable

            $stmt->bindParam(':idUsuario', $idUsuario);
            $stmt->bindParam(':descripcion', $descripcion);
            $stmt->bindParam(':fechaHora', $fechaHora);
            $stmt->bindParam(':sintomas', $sintomas);

            //ejecución de la consulta

            if ($stmt->execute()) {
                return ['status' => 'success', 'message' => 'Anomalia creada exitosamente.'];
            } else {
                return ['status' => 'error', 'message' => 'Error al crear la anomalia.'];
            }
        }

        //captura de excepciones
        catch (Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }

    }

    public function obtenerAnomalias($idUsuario): array {
        try {
            $sql = "SELECT * FROM anomalia WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC";
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