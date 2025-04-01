<?php
// routes/glucosaRoutes.php

require_once '../config/database.php';
require_once '../controllers/GlucosaController.php';

$db = new Conexion();
$conn = $db->conectar();

if (!$conn) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit;
}

$controller = new GlucosaController($conn);
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!$data) {
        echo json_encode(['status' => 'error', 'message' => 'Datos mal formateados o vacíos.']);
        exit;
    }

    $response = $controller->crearRegistro($data);
    echo json_encode($response);
} elseif ($method === 'GET') {
    if (isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) && $_GET['idUsuario'] > 0) {
        $idUsuario = $_GET['idUsuario'];
        $response = $controller->obtenerRegistros($idUsuario);
        echo json_encode($response);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'ID de usuario no proporcionado o inválido.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Método HTTP no soportado.']);
}
?>