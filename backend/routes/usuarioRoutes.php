<?php
// routes/usuarioRoutes.php

// --- Cabeceras ---
// Permitir métodos específicos, configurar tipo de contenido y CORS si es necesario
header("Access-Control-Allow-Origin: *"); // Ajusta '*' a tu dominio en producción
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Manejar solicitud OPTIONS preflight (necesario para algunos requests CORS)
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// --- Dependencias ---
require_once '../config/database.php';
require_once '../models/Usuario.php'; // Contiene Modelo, Entidad, Factory
require_once '../controllers/UsuarioController.php';

// --- Conexión a BD ---
try {
    $db = new Conexion();
    $conn = $db->conectar();
} catch (Exception $e) {
    http_response_code(503); // Service Unavailable
    echo json_encode(["success" => false, "message" => "Error de conexión DB: " . $e->getMessage()]);
    exit;
}

// --- Inyección de Dependencias ---
try {
    $usuarioModel = new UsuarioModel($conn);
    $usuarioController = new UsuarioController($usuarioModel);
} catch(Exception $e) {
     http_response_code(500); // Internal Server Error
     echo json_encode(["success" => false, "message" => "Error inicializando componentes: " . $e->getMessage()]);
     exit;
}


// --- Enrutamiento Principal ---
$method = $_SERVER['REQUEST_METHOD'];
$response = ["success" => false, "message" => "Solicitud no válida."]; // Default response
$http_status_code = 400; // Bad Request by default

// Determinar la acción (simplificado - una app real usaría un router más robusto)
// Aquí asumimos que la URL base es para usuarios, y acciones específicas como login
// podrían tener una ruta diferente o un parámetro 'action'.
$action = $_GET['action'] ?? null; // Ejemplo: ?action=login, ?action=register

$isLoginRequest = isset($_GET['login']);
$isRegisterRequest = isset($_GET['register']);
// Extraer ID si viene en la URL para PUT/DELETE/GET(single)
$idUsuario = isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) ? (int)$_GET['idUsuario'] : null;

try {
    switch ($method) {
        case 'POST':
            $data = null; // Inicializar $data
             $action = $_GET['action'] ?? null; // Obtener acción

             $data = $_POST;
             if (empty($data)) {
                  $response = ["success" => false, "message" => "No se recibieron datos del formulario."];
                  $http_status_code = 400;
                  break; // Salir del switch
             }

            // Diferenciar acción POST
            if ($isLoginRequest) { // Detectado por ?login en la URL
                $response = $usuarioController->loginUsuario($data);

                // --- MANEJO DE REDIRECCIÓN LOGIN ---
                session_start(); // Asegurar que la sesión esté activa para guardar mensajes
                if ($response['success']) {
                    // ¡Éxito! Usuario logueado y variables de sesión creadas por el controller.
                    // Redirigir a la página principal de la aplicación (ej. registro de glucosa)
                    // Ajusta esta ruta según tu estructura
                    header('Location: ../public/registroGlucosa.php'); // O dashboard.php, etc.
                    exit();
                } else {
                    // Falló el login
                    $_SESSION['message'] = $response['message']; // Guardar mensaje de error
                    $_SESSION['message_type'] = 'error';
                    // Redirigir DE VUELTA a la página de login
                    header('Location: ../public/login.php'); // Ajusta si es necesario
                    exit();
                }
                // --- FIN MANEJO REDIRECCIÓN ---

            } elseif ($isRegisterRequest) { // Detectado por ?register en la URL
                $response = $usuarioController->registrarUsuario($data);

                 // --- MANEJO DE REDIRECCIÓN REGISTRO ---
                 session_start(); // Asegurar sesión
                 $_SESSION['message'] = $response['message']; // Guardar mensaje (éxito o error)

                 if ($response['success']) {
                     $_SESSION['message_type'] = 'success';
                     // Redirigir a login después de registro exitoso
                     header('Location: ../public/login.php'); // Ajusta si es necesario
                     exit();
                 } else {
                      $_SESSION['message_type'] = 'error';
                      // Redirigir DE VUELTA a registro si falló (para mostrar error y reintentar)
                      // Necesitarías también guardar los datos ingresados (excepto password) en sesión
                      // para repoblar el formulario, o manejarlo de otra forma.
                      // Por simplicidad ahora, solo redirigimos a registro vacío con error:
                      header('Location: ../public/registrousuario.php'); // Ajusta si es necesario
                      exit();
                    }
             } elseif ($action === 'register' /* || $action === null */ ) { // Ajusta condición si es necesario
                 // --- CORRECCIÓN AQUÍ ---
                 // Para registro desde el FORMULARIO HTML, leer desde $_POST
                 $data = $_POST;
                 if (empty($data)) {
                      $response = ["success" => false, "message" => "No se recibieron datos del formulario de registro."];
                      $http_status_code = 400;
                      break; // Salir del switch
                 }
                 // --- FIN CORRECCIÓN ---

                 $response = $usuarioController->registrarUsuario($data);
                 // El controller ya valida campos dentro de $data
                 $http_status_code = $response['success'] ? 201 : 400;

                 // --- Opcional: Redirección después de registro exitoso (para forms HTML) ---
                 if ($response['success']) {
                     session_start(); // Asegurar sesión iniciada
                     $_SESSION['message'] = $response['message'];
                     $_SESSION['message_type'] = 'success';
                     // Redirigir a login o a una página de éxito
                     header('Location:../public/login.php'); // Ajusta la ruta según tu estructura para el login.php del frontend
                     exit(); // Detener ejecución después de redirigir
                 }
                 // Si no fue exitoso, el script continuará y mostrará el JSON de error (o podrías redirigir con mensaje de error)


             } else {
                  $response = ["success" => false, "message" => "Acción POST no reconocida."];
                  $http_status_code = 400;
             }
            break; // Fin de case 'POST'

        case 'GET':
            // Podría ser para obtener perfil de usuario logueado, o un usuario específico por ID (admin?), etc.
             // Ejemplo: Obtener datos de un usuario por ID (requiere método en Controller/Model)
             /*
             if ($idUsuario !== null) {
                 // $response = $usuarioController->obtenerPerfilUsuario($idUsuario); // Necesitarías crear este método
                 // $http_status_code = $response['success'] ? 200 : 404;
             } else {
                   // $response = $usuarioController->obtenerPerfilPropio(); // Obtener perfil del usuario logueado ($_SESSION)
                   // $http_status_code = $response['success'] ? 200 : 401; // O 401 si no está logueado
                    $response = ["success" => false, "message" => "Funcionalidad GET no implementada completamente."];
                    $http_status_code = 501; // Not Implemented
             }
             */
             $response = ["success" => false, "message" => "Funcionalidad GET no implementada."];
             $http_status_code = 501; // Not Implemented
            break;

        case 'PUT':
            // --- ACTUALIZAR Usuario ---
            if ($idUsuario !== null) {
                 $data = json_decode(file_get_contents("php://input"), true);
                 if (json_last_error() !== JSON_ERROR_NONE) {
                     $response = ["success" => false, "message" => "Error en JSON recibido."];
                     $http_status_code = 400;
                     break;
                 }
                 if (empty($data)) {
                      $response = ["success" => false, "message" => "No se enviaron datos para actualizar."];
                      $http_status_code = 400;
                      break;
                 }

                 $response = $usuarioController->actualizarUsuario($idUsuario, $data);

                 // Ajustar código HTTP según el resultado y la lógica de autorización (que debe estar en el controller)
                 if ($response['success']) {
                     $http_status_code = 200; // OK
                 } elseif (strpos($response['message'], 'No autorizado') !== false) {
                     $http_status_code = 403; // Forbidden
                 } elseif (strpos($response['message'], 'No se encontró') !== false) {
                      $http_status_code = 404; // Not Found
                 } else {
                      $http_status_code = 400; // Bad Request (ej. error de validación)
                 }

            } else {
                 $response = ["success" => false, "message" => "Se requiere idUsuario en la URL (?idUsuario=...) para actualizar."];
                 $http_status_code = 400;
            }
            break;

        case 'DELETE':
            // --- ELIMINAR Usuario ---
            if ($idUsuario !== null) {
                 $response = $usuarioController->eliminarUsuario($idUsuario);

                 // Ajustar código HTTP según el resultado y la autorización
                 if ($response['success']) {
                     $http_status_code = 200; // OK (o 204 No Content si no devuelves body)
                 } elseif (strpos($response['message'], 'No autorizado') !== false) {
                     $http_status_code = 403; // Forbidden
                 } elseif (strpos($response['message'], 'no exista') !== false || strpos($response['message'], 'No se pudo eliminar') !== false) {
                      $http_status_code = 404; // Not Found (o 400/500 si falló por otra razón)
                 } else {
                       $http_status_code = 500; // Internal Server Error (si es error inesperado)
                 }

            } else {
                 $response = ["success" => false, "message" => "Se requiere idUsuario en la URL (?idUsuario=...) para eliminar."];
                 $http_status_code = 400;
            }
            break;

        default:
            $response = ["success" => false, "message" => "Método HTTP no soportado."];
            $http_status_code = 405; // Method Not Allowed
            break;
    }
} catch (Exception $e) {
    // Captura cualquier excepción no manejada
    error_log("Error general en usuarioRoutes: " . $e->getMessage());
    $response = ["success" => false, "message" => "Ocurrió un error interno en el servidor."];
    $http_status_code = 500;
}

// --- Enviar Respuesta Final ---
http_response_code($http_status_code);
echo json_encode($response);

?>