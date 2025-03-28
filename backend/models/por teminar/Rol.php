<?php

require_once __DIR__ . 'backend\config\database.php';

class Rol {
    private $conexion;

    public function __construct($conexion) {
        $this->conexion = $conexion;
    }

    public function ObtenerRoles() {
        $query = "SELECT * FROM rol";
        $stmt = $this->conexion->prepare($query);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>