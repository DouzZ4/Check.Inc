<?php
// routes/glucosaRoutes.php

// Permitir métodos específicos (CORS si es necesario en el futuro)
header("Access-Control-Allow-Origin: *"); // O especifica tu dominio
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Manejar solicitud OPTIONS preflight (para CORS)
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}


require_once '../config/database.php';
require_once '../controllers/GlucosaController.php';
// Asegúrate de que el archivo con la Entidad Glucosa y Factory también esté disponible
require_once '../models/GlucosaModel.php'; // Contiene Glucosa y GlucosaFactory

// --- Conexión a BD ---
// Asume que tu clase Conexion está en database.php
try {
    $db = new Conexion(); // O como sea que obtengas la conexión
    $conn = $db->conectar(); // Asume que esto devuelve un objeto PDO
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); // Recomendado para manejar errores PDO
} catch (PDOException $e) {
    http_response_code(500); // Internal Server Error
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos: ' . $e->getMessage()]);
    exit;
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Error general al conectar: ' . $e->getMessage()]);
    exit;
}

$controller = new GlucosaController($conn);
$response = ['status' => 'error', 'message' => 'Solicitud no válida o método no soportado.']; // Respuesta por defecto
$http_status_code = 400; // Bad Request por defecto

// --- Enrutamiento basado en el Método HTTP ---

$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'POST':
            // --- CREAR un nuevo registro ---
            $data = json_decode(file_get_contents("php://input"), true);

            // Mejor validación de JSON
            if (json_last_error() !== JSON_ERROR_NONE) {
                 $response = ['status' => 'error', 'message' => 'Error decodificando JSON: ' . json_last_error_msg()];
                 break; // Sale del switch
            }

            // El controlador ya valida campos específicos dentro
            $response = $controller->crearRegistro($data);
            $http_status_code = ($response['status'] === 'success') ? 201 : 400; // 201 Created or 400 Bad Request
            break;

        case 'GET':
            // --- LEER registros (todos de un usuario O uno específico) ---
            if (isset($_GET['idGlucosa']) && is_numeric($_GET['idGlucosa']) && $_GET['idGlucosa'] > 0) {
                // Obtener UN registro por idGlucosa
                $idGlucosa = (int)$_GET['idGlucosa'];
                $response = $controller->obtenerRegistroPorId($idGlucosa); // Necesitamos añadir este método al controller
                 $http_status_code = ($response['status'] === 'success') ? 200 : (($response['message'] === 'Registro no encontrado.') ? 404 : 400);
            } elseif (isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) && $_GET['idUsuario'] > 0) {
                // Obtener TODOS los registros de un idUsuario
                $idUsuario = (int)$_GET['idUsuario'];
                $response = $controller->obtenerRegistros($idUsuario);
                 $http_status_code = ($response['status'] === 'success') ? 200 : 400;
            } else {
                $response = ['status' => 'error', 'message' => 'Falta idUsuario o idGlucosa válido para GET.'];
                 $http_status_code = 400;
            }
            break;

        case 'PUT':
             // --- ACTUALIZAR un registro existente ---
            if (isset($_GET['idGlucosa']) && is_numeric($_GET['idGlucosa']) && $_GET['idGlucosa'] > 0) {
                $idGlucosa = (int)$_GET['idGlucosa'];
                $data = json_decode(file_get_contents("php://input"), true);

                if (json_last_error() !== JSON_ERROR_NONE) {
                    $response = ['status' => 'error', 'message' => 'Error decodificando JSON: ' . json_last_error_msg()];
                    break;
                }

                if (empty($data)) {
                     $response = ['status' => 'error', 'message' => 'No se enviaron datos para actualizar en el cuerpo JSON.'];
                     break;
                }

                $response = $controller->actualizarRegistro($idGlucosa, $data);
                $http_status_code = ($response['status'] === 'success') ? 200 : (($response['status'] === 'info') ? 200 : 400); // OK, OK(info), or Bad Request
                 // Podrías usar 404 si el ID no existe basado en la respuesta del controller
                 if($response['status'] === 'info' && strpos($response['message'], 'no existe') !== false) {
                     $http_status_code = 404; // Not Found
                 }


            } else {
                $response = ['status' => 'error', 'message' => 'Falta idGlucosa válido en la URL para PUT.'];
                 $http_status_code = 400;
            }
            break;

        case 'DELETE':
            // --- ELIMINAR un registro existente ---
             if (isset($_GET['idGlucosa']) && is_numeric($_GET['idGlucosa']) && $_GET['idGlucosa'] > 0) {
                 $idGlucosa = (int)$_GET['idGlucosa'];
                 $response = $controller->eliminarRegistro($idGlucosa);
                  $http_status_code = ($response['status'] === 'success') ? 200 : (($response['status'] === 'info') ? 404 : 500); // OK, Not Found, or Internal Error
                  // Si el controller devuelve 'info' porque no existía, devolvemos 404
             } else {
                 $response = ['status' => 'error', 'message' => 'Falta idGlucosa válido en la URL para DELETE.'];
                 $http_status_code = 400;
             }
            break;

        default:
            $response = ['status' => 'error', 'message' => 'Método HTTP no soportado: ' . $method];
            $http_status_code = 405; // Method Not Allowed
            break;
    }
} catch (Exception $e) {
    // Captura excepciones generales o las lanzadas por el controller/entidad
    $http_status_code = 500; // Internal Server Error
    $response = ['status' => 'error', 'message' => 'Error inesperado: ' . $e->getMessage()];
    // En producción, podrías querer loguear $e->getMessage() pero no mostrarlo al usuario
}

// --- Enviar Respuesta ---
http_response_code($http_status_code);
echo json_encode($response);

?>