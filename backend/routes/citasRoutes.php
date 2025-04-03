<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");

require_once '../config/database.php'; // Para la conexión
require_once '../models/CitasModel.php';   // Contiene CitasModel, Cita, CitaFactory
require_once '../controllers/citasController.php';

try {
    $db = new Conexion(); // O tu método de conexión
    $conn = $db->conectar(); // Objeto PDO
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión DB: ' . $e->getMessage()]);
    exit;
}

// Instanciar el Modelo con la conexión
$citasModel = new CitaModel($conn);

// Instanciar el Controlador pasando el Modelo
$citasController = new CitasController($citasModel);

// Enrutamiento (el switch/case como lo tenías antes)
$method = $_SERVER['REQUEST_METHOD'];
$response = ['status' => 'error', 'message' => 'Solicitud no válida o método no soportado.'];
$http_status_code = 400;

try {
    switch ($method) {
        case 'POST':
            $data = json_decode(file_get_contents("php://input"), true);
            if (json_last_error() !== JSON_ERROR_NONE) {
                throw new Exception('Error al decodificar JSON: ' . json_last_error_msg());
            }
            $response = $citasController->crearCita($data);
            $http_status_code = ($response['status'] === 'success') ? 201 : 400;
            break;

        case 'GET':
            if (isset($_GET['idUsuario'])) {
                $idUsuario = (int)$_GET['idUsuario'];
                $response = $citasController->obtenerCitas($idUsuario);
                $http_status_code = ($response['status'] === 'success') ? 200 : 400;
            } else {
                throw new Exception('Falta el parámetro idUsuario.');
            }
            break;

        case 'PUT':
            $data = json_decode(file_get_contents("php://input"), true);
            if (isset($_GET['idCita'])) {
                $idCita = (int)$_GET['idCita'];
                $response = $citasController->actualizarCita($idCita, $data);
                $http_status_code = ($response['status'] === 'success') ? 200 : 400;
            } else {
                throw new Exception('Falta el parámetro idCita.');
            }
            break;

        case 'DELETE':
            if (isset($_GET['idCita'])) {
                $idCita = (int)$_GET['idCita'];
                $response = $citasController->eliminarCita($idCita);
                $http_status_code = ($response['status'] === 'success') ? 200 : 400;
            } else {
                throw new Exception('Falta el parámetro idCita.');
            }
            break;

        default:
            throw new Exception('Método no soportado.');
    }
} catch (Exception $e) {
    $http_status_code = 500;
    $response = ['status' => 'error', 'message' => 'Error inesperado en la ruta: ' . $e->getMessage()];
}

http_response_code($http_status_code);
echo json_encode($response);
?>