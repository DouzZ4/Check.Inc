<?php
require_once __DIR__ . '../config/database.php';

$conexion = new Conexion();
$db = $conexion->conectar();

if ($db) {
    echo "Conexión exitosa!";
} else {
    echo "Error al conectar.";
}
?>