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
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['idUsuario'], $data['nivelGlucosa'], $data['fechaHora'])) {
        echo json_encode(['status' => 'error', 'message' => 'Faltan datos obligatorios.']);
        exit;
    }

    $response = $controller->crearRegistro($data);
    echo json_encode($response);
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) && $_GET['idUsuario'] > 0) {
        $idUsuario = $_GET['idUsuario'];
        $response = $controller->obtenerRegistros($idUsuario);
        echo json_encode($response);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'ID de usuario no válido.']);
    }
}
?>