<?php
require_once 'config/database.php';

try {
    $conexion = Conexion::conectar();
    echo "Conexión exitosa a la base de datos.";
} catch (Exception $e) {
    echo "Error al conectar a la base de datos: " . $e->getMessage();
}
?>