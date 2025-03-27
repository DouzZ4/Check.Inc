<?php
require_once '../config/database.php';
require_once '../controllers/glucosaController.php';

$db = new Conexion();
$conn = $db->conectar();
$controller = new GlucosaController($conn);

$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    $response = $controller->crearRegistro($data);
    echo json_encode($response);
} elseif ($method === 'GET') {
    if (isset($_GET['idUsuario'])) {
        $idUsuario = $_GET['idUsuario'];
        $response = $controller->obtenerRegistros($idUsuario);
        echo json_encode($response);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'ID de usuario no proporcionado']);
    }
}
?>