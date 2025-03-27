<?php

require_once __DIR__ . '/Rol.php';

class Usuario extends Rol {
    private $db;

    public function __construct() {
        parent::__construct(); // Llama al constructor de la clase Rol
        $this->db = Conexion::conectar();
    }

    public function registrarUsuario($user, $password, $documento, $nombres, $apellidos, $correo, $edad, $idRol) {
        $query = "INSERT INTO Usuario (user, password, documento, nombres, apellidos, correo, edad, idRol) VALUES (:user, :password, :documento, :nombres, :apellidos, :correo, :edad, :idRol)";
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':user', $user);
        $stmt->bindParam(':password', $password);
        $stmt->bindParam(':documento', $documento);
        $stmt->bindParam(':nombres', $nombres);
        $stmt->bindParam(':apellidos', $apellidos);
        $stmt->bindParam(':correo', $correo);
        $stmt->bindParam(':edad', $edad);
        $stmt->bindParam(':idRol', $idRol);

        return $stmt->execute();
    }
}
