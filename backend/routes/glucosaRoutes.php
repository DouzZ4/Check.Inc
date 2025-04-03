<?php
// routes/glucosaRoutes.php

// Cabeceras CORS y Content-Type (como las tenías)
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
// ... (resto de cabeceras y manejo de OPTIONS) ...

// Requerir dependencias
require_once '../config/database.php'; // Para la conexión
require_once '../models/GlucosaModel.php';   // Contiene GlucosaModel, Glucosa, GlucosaFactory
require_once '../controllers/GlucosaController.php'; // El controlador refactorizado

// --- Conexión a BD (como antes) ---
try {
    $db = new Conexion(); // O tu método de conexión
    $conn = $db->conectar(); // Objeto PDO
    // No es necesario setear ERRMODE aquí si el Modelo lo hace
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión DB: ' . $e->getMessage()]);
    exit;
}

// --- ¡CAMBIO IMPORTANTE! ---
// 1. Instanciar el Modelo con la conexión
$glucosaModel = new GlucosaModel($conn);

// 2. Instanciar el Controlador pasando el Modelo
$controller = new GlucosaController($glucosaModel);
// --- FIN CAMBIO IMPORTANTE ---


// --- Enrutamiento (el switch/case como lo tenías antes) ---
$method = $_SERVER['REQUEST_METHOD'];
$response = ['status' => 'error', 'message' => 'Solicitud no válida o método no soportado.'];
$http_status_code = 400;

try {
    switch ($method) {
        case 'POST':
            // ... (tu lógica para obtener $data de php://input) ...
            // ... (validar JSON) ...
            $response = $controller->crearRegistro($data); // Llamada al controller igual que antes
            $http_status_code = ($response['status'] === 'success') ? 201 : 400;
            break;

        case 'GET':
             // ... (tu lógica para ver si es por idGlucosa o idUsuario) ...
              if (isset($_GET['idGlucosa']) /*...*/) {
                  $idGlucosa = (int)$_GET['idGlucosa'];
                  $response = $controller->obtenerRegistroPorId($idGlucosa); // Llamada igual
                  $http_status_code = ($response['status'] === 'success') ? 200 : (($response['message'] === 'Registro no encontrado.') ? 404 : 400);
              } elseif (isset($_GET['idUsuario']) /*...*/) {
                  $idUsuario = (int)$_GET['idUsuario'];
                  $response = $controller->obtenerRegistros($idUsuario); // Llamada igual
                  $http_status_code = ($response['status'] === 'success') ? 200 : 400;
              } else {
                 // ... (error por falta de parámetro) ...
              }
            break;

        case 'PUT':
             // ... (tu lógica para obtener idGlucosa y $data) ...
              if (isset($_GET['idGlucosa']) /*...*/) {
                  $idGlucosa = (int)$_GET['idGlucosa'];
                  // ... (obtener $data de php://input, validar JSON, validar $data no vacío) ...
                  $response = $controller->actualizarRegistro($idGlucosa, $data); // Llamada igual
                  // ... (tu lógica para http_status_code basada en la respuesta) ...
              } else {
                 // ... (error por falta de idGlucosa) ...
              }
            break;

        case 'DELETE':
             // ... (tu lógica para obtener idGlucosa) ...
              if (isset($_GET['idGlucosa']) /*...*/) {
                  $idGlucosa = (int)$_GET['idGlucosa'];
                  $response = $controller->eliminarRegistro($idGlucosa); // Llamada igual
                  // ... (tu lógica para http_status_code basada en la respuesta) ...
              } else {
                  // ... (error por falta de idGlucosa) ...
              }
            break;

        default:
             // ... (error 405 Method Not Allowed) ...
            break;
    }
} catch (Exception $e) {
    // Captura excepciones generales que puedan ocurrir en el router o controller
    $http_status_code = 500;
    $response = ['status' => 'error', 'message' => 'Error inesperado en la ruta: ' . $e->getMessage()];
}

// --- Enviar Respuesta (igual que antes) ---
http_response_code($http_status_code);
echo json_encode($response);

?>