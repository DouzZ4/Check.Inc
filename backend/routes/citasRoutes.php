<?php
// routes/citasRoutes.php

require_once "../config/database.php";
require_once "../controllers/citaController.php";

$db = new Conexion();
$conn = $db->conectar();

if (!$conn) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit;
}

$controller = new CitaController($conn);
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['fecha'], $data['hora'], $data['motivo'], $data['idUsuario'])) {
        echo json_encode(['status' => 'error', 'message' => 'Faltan datos obligatorios.']);
        exit;
    }

    $response = $controller->crearCita($data);
    echo json_encode($response);
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) && $_GET['idUsuario'] > 0) {
        $idUsuario = $_GET['idUsuario'];
        $response = $controller->obtenerCitas($idUsuario);
        echo json_encode($response);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'ID de usuario no válido.']);
    }
}
?>