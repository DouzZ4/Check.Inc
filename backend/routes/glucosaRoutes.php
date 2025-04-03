<?php
// routes/glucosaRoutes.php

// --- Cabeceras ---
header("Access-Control-Allow-Origin: *"); // Ajusta en producción
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Manejar solicitud OPTIONS preflight
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// --- Dependencias ---
require_once '../config/database.php'; // Para la conexión
require_once '../models/GlucosaModel.php';   // Contiene GlucosaModel, Glucosa, GlucosaFactory
require_once '../controllers/GlucosaController.php'; // El controlador refactorizado

// --- Conexión a BD ---
try {
    $db = new Conexion();
    $conn = $db->conectar();
} catch (Exception $e) {
    http_response_code(503); // Service Unavailable
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión DB: ' . $e->getMessage()]);
    exit;
}

// --- Inyección de Dependencias ---
try {
    $glucosaModel = new GlucosaModel($conn);
    $controller = new GlucosaController($glucosaModel);
} catch(Exception $e) {
     http_response_code(500);
     echo json_encode(['status' => 'error', 'message' => 'Error inicializando componentes: ' . $e->getMessage()]);
     exit;
}

// --- Enrutamiento ---
$method = $_SERVER['REQUEST_METHOD'];
$response = ['status' => 'error', 'message' => 'Solicitud no válida o método no soportado.'];
$http_status_code = 400;

// Extraer IDs de parámetros GET (si existen)
$idGlucosa = isset($_GET['idGlucosa']) && is_numeric($_GET['idGlucosa']) ? (int)$_GET['idGlucosa'] : null;
$idUsuario = isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) ? (int)$_GET['idUsuario'] : null;

// Variable para datos de entrada (POST/PUT)
$data = null;

try {
    switch ($method) {
        case 'POST':
            // --- CREAR Registro Glucosa (espera JSON) ---
            $data = json_decode(file_get_contents("php://input"), true);

            // Validar JSON recibido
            if (json_last_error() !== JSON_ERROR_NONE) {
                $response = ["status" => false, "message" => "Error decodificando JSON: " . json_last_error_msg()];
                $http_status_code = 400;
                break; // Salir del switch
            }
            // Validar que $data sea un array (podría ser null si json_decode falla o lee 'null')
            if (!is_array($data)) {
                 $response = ["status" => false, "message" => "No se recibieron datos válidos en formato JSON."];
                 $http_status_code = 400;
                 break; // Salir del switch
            }

            // Llamar al controlador (que valida campos obligatorios dentro de $data)
            $response = $controller->crearRegistro($data);
            $http_status_code = ($response['status'] === 'success') ? 201 : 400;
            break;

        case 'GET':
            // --- LEER Registros (por idGlucosa o idUsuario) ---
            if ($idGlucosa !== null) {
                $response = $controller->obtenerRegistroPorId($idGlucosa);
                $http_status_code = ($response['status'] === 'success') ? 200 : (($response['message'] === 'Registro no encontrado.') ? 404 : 400);
            } elseif ($idUsuario !== null) {
                $response = $controller->obtenerRegistros($idUsuario);
                $http_status_code = ($response['status'] === 'success') ? 200 : 400;
            } else {
               $response = ['status' => 'error', 'message' => 'Falta idUsuario o idGlucosa válido para GET.'];
               $http_status_code = 400;
            }
            break;

        case 'PUT':
            // --- ACTUALIZAR Registro Glucosa (espera JSON) ---
            if ($idGlucosa !== null) {
                 $data = json_decode(file_get_contents("php://input"), true);

                 // Validar JSON recibido
                 if (json_last_error() !== JSON_ERROR_NONE) {
                     $response = ["status" => false, "message" => "Error decodificando JSON: " . json_last_error_msg()];
                     $http_status_code = 400;
                     break;
                 }
                 // Validar que $data sea array y no esté vacío para PUT
                 if (!is_array($data) || empty($data)) {
                      $response = ["status" => false, "message" => "No se recibieron datos válidos en formato JSON para actualizar."];
                      $http_status_code = 400;
                      break;
                 }

                 // Llamar al controlador
                 $response = $controller->actualizarRegistro($idGlucosa, $data);

                 // Establecer código HTTP
                 if ($response['status'] === 'success') { $http_status_code = 200; }
                 elseif ($response['status'] === 'info') { $http_status_code = 200; } // O podrías usar 304 Not Modified si no hubo cambios
                 elseif (strpos($response['message'], 'no encontrado') !== false) { $http_status_code = 404; }
                 // Aquí podrías añadir chequeo para 403 Forbidden si implementas autorización
                 else { $http_status_code = 400; } // Otros errores (validación, etc.)

            } else {
                 $response = ['status' => 'error', 'message' => 'Se requiere idGlucosa en la URL (?idGlucosa=...) para actualizar.'];
                 $http_status_code = 400;
            }
            break;

        case 'DELETE':
            // --- ELIMINAR Registro Glucosa ---
            if ($idGlucosa !== null) {
                 $response = $controller->eliminarRegistro($idGlucosa);

                 // Establecer código HTTP
                 if ($response['status'] === 'success') { $http_status_code = 200; } // OK (o 204 No Content)
                 elseif ($response['status'] === 'info') { $http_status_code = 404; } // Not Found
                 // Aquí podrías añadir chequeo para 403 Forbidden
                 else { $http_status_code = 500; } // Error inesperado al eliminar

            } else {
                 $response = ['status' => 'error', 'message' => 'Se requiere idGlucosa en la URL (?idGlucosa=...) para eliminar.'];
                 $http_status_code = 400;
            }
            break;

        default:
            $response = ['status' => 'error', 'message' => 'Método HTTP no soportado.'];
            $http_status_code = 405; // Method Not Allowed
            break;
    }
} catch (Exception $e) {
    // --- Manejo General de Excepciones (Siempre devuelve JSON) ---
    error_log("ERROR General en ruta Glucosa: " . $e->getMessage() . " en " . $e->getFile() . ":" . $e->getLine());
    $http_status_code = 500;
    $response = ['status' => 'error', 'message' => 'Ocurrió un error interno en el servidor.'];
    // Podrías añadir detalles del error en desarrollo:
    // $response['details'] = $e->getMessage();
}

// --- Enviar Respuesta Final JSON ---
http_response_code($http_status_code);
echo json_encode($response);

?>