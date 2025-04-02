<?php
// routes/citasRoutes.php

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../controllers/citaController.php';
require_once __DIR__ . '/../models/CitasModel.php';
require_once __DIR__ . '/../models/Usuario.php';

$db = new Conexion();
$conn = $db->conectar();

if (!$conn) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit;
}

// Instancia del controlador para manejar las solicitudes
$controller = new CitaController($conn);

// Manejo de solicitudes POST (crear cita)
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['fecha'], $data['hora'], $data['motivo'], $data['idUsuario'])) {
        echo json_encode(['status' => 'error', 'message' => 'Faltan datos obligatorios.']);
        exit;
    }

    $response = $controller->crearCita($data);
    echo json_encode($response);
    exit;
}

// Manejo de solicitudes GET (obtener citas por usuario)
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) && $_GET['idUsuario'] > 0) {
        $idUsuario = $_GET['idUsuario'];
        $response = $controller->obtenerCitas($idUsuario);
        echo json_encode($response);
        exit;
    } else {
        echo json_encode(['status' => 'error', 'message' => 'ID de usuario no válido.']);
        exit;
    }
}

// Si el método no es permitido, devuelve un error
http_response_code(405);
echo json_encode(['status' => 'error', 'message' => 'Método no permitido.']);
exit;
?>