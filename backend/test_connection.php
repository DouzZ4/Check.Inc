<?php
require_once 'C:\xampp\htdocs\check.inc\backend\config\database.php';

$conexion = new Conexion();
$db = $conexion->conectar();

if ($db) {
    echo "Conexión exitosa!";
} else {
    echo "Error al conectar.";
}
?>