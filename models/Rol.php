<?php

require_once __DIR__ . '/../config/database.php';

class Rol {
    private $db;

    public function __construct() {
        $this->db = Conexion::conectar();
    }

    public function ObtenerRoles() {
        $query = "SELECT * FROM rol";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>