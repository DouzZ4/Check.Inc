<?php
class Conexion {
    private static $database = 'Checks';
    private static $user = 'root';
    private static $password = '';
    private static $host = 'localhost';

    public static function conectar() {
        try {
            $conexion = new PDO("mysql:host=localhost;dbname=Checks", "root", "");
            $conexion->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            return $conexion;
        } catch (PDOException $e) {
            die("Error en la conexión: " . $e->getMessage());
        }
    }
}
?>