<?php
$host = "localhost";
$user = "tu_usuario";  // Reemplaza con el usuario correcto
$password = "tu_contraseña";  // Reemplaza con la clave correcta
$dbname = "nombre_de_tu_base_de_datos";

$conn = new mysqli($host, $user, $password, $dbname);

// Verifica si hay errores en la conexión
if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
} else {
    echo "Conexión exitosa a la base de datos.";
}
?>
