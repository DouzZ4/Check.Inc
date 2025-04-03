<?php
// routes/AnomaliaRoutes.php

require_once '../config/database.php';
require_once '../controllers/AnomaliaController.php';

$db = new Conexion();
$conn = $db->conectar();

// verfifca si la conexión es exitosa
if(!$conn) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos.']);
    exit;
}

// Instancia del controlador
$controller = new AnomaliaController($conn);

// Manejo de la solicitud POST para crear una anomalia
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    // Verifica si los datos obligatorios están presentes
    if (!isset($data['idUsuario'], $data['descripcion'], $data['fechaHora'], $data['sintomas'])) {
        echo json_encode(['status' => 'error', 'message' => 'Faltan datos obligatorios.']);
        exit;
    }

    // Llama al método para crear la anomalia
    $response = $controller->crearAnomalia($data);
    echo json_encode($response);
}

// Manejo de la solicitud GET para obtener anomalias
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    // Verifica si el ID de usuario es válido
    if (isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) && $_GET['idUsuario'] > 0) {
        $idUsuario = $_GET['idUsuario'];
        $response = $controller->obtenerAnomalias($idUsuario);
        echo json_encode($response);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'ID de usuario no válido.']);
    }
}

?>