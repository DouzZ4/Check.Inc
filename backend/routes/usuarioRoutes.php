<?php
// routes/usuarioRoutes.php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL); // Mantenlo para desarrollo, quítalo en producción

// --- Cabeceras ---
header("Access-Control-Allow-Origin: *"); // Ajusta '*' a tu dominio en producción
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

// --- Dependencias ---
require_once '../config/database.php';
require_once '../models/Usuario.php';
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
$response = ["success" => false, "message" => "Solicitud no válida."];
$http_status_code = 400;

// Determinar la acción y el ID de usuario si aplica
$action = $_GET['action'] ?? null;
$idUsuario = isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) ? (int)$_GET['idUsuario'] : null;

try {
    switch ($method) {
        case 'POST':
            $data = $_POST; // Leer datos del formulario
             if (empty($data)) {
                 $response = ["success" => false, "message" => "No se recibieron datos del formulario."];
                 $http_status_code = 400;
                 break;
             }

            // --- Usar $action para dirigir ---
            if ($action === 'login') {
                $response = $usuarioController->loginUsuario($data); // Esta función ya guarda sesión si éxito

                // --- INICIO: LÓGICA DE REDIRECCIÓN LOGIN (CORREGIDA) ---
                if ($response['success']) {
                    // Asegurar que la sesión esté activa para leer el rol
                    if (session_status() === PHP_SESSION_NONE) {
                        session_start();
                    }

                    // Verificar el rol guardado en la sesión
                    if (isset($_SESSION['rolUsuario'])) {
                        if ($_SESSION['rolUsuario'] == 1) { // Asumiendo 1 = Admin
                            // Redirigir al Dashboard de Admin
                            header('Location: ../public/dashboard_admin.php');
                            exit();
                        } elseif ($_SESSION['rolUsuario'] == 2) { // Asumiendo 2 = Paciente
                            // Redirigir a la página de Glucosa (o su dashboard)
                            header('Location: ../public/registroGlucosa.php');
                            exit();
                        } else {
                            // Rol desconocido, enviar a inicio genérico
                            header('Location: ../../index.php'); // Ajusta si es necesario
                            exit();
                        }
                    } else {
                        // Error interno si no se guardó el rol
                        error_log("Error crítico: rolUsuario no encontrado en sesión post-login para idUsuario: " . ($_SESSION['idUsuario'] ?? 'DESCONOCIDO'));
                        if (session_status() === PHP_SESSION_NONE) { session_start(); }
                        $_SESSION['message'] = 'Error interno al iniciar sesión (ERR_ROL).';
                        $_SESSION['message_type'] = 'error';
                        header('Location: ../public/login.php');
                        exit();
                    }
                } else {
                    // Falló el login (credenciales incorrectas, etc.)
                     if (session_status() === PHP_SESSION_NONE) { session_start(); }
                    $_SESSION['message'] = $response['message'];
                    $_SESSION['message_type'] = 'error';
                    header('Location: ../public/login.php');
                    exit();
                }
                // --- FIN: LÓGICA DE REDIRECCIÓN LOGIN (CORREGIDA) ---

            } elseif ($action === 'register') {
                $response = $usuarioController->registrarUsuario($data);
                // --- MANEJO DE REDIRECCIÓN REGISTRO ---
                if (session_status() === PHP_SESSION_NONE) { session_start(); }
                $_SESSION['message'] = $response['message'];

                if ($response['success']) {
                    $_SESSION['message_type'] = 'success';
                    header('Location: ../public/login.php');
                    exit();
                } else {
                    $_SESSION['message_type'] = 'error';
                    header('Location: ../public/registrousuario.php');
                    exit();
                }
                 // --- FIN MANEJO REDIRECCIÓN REGISTRO ---

            } else {
                 $response = ["success" => false, "message" => "Acción POST no reconocida. Falta ?action=login o ?action=register"];
                 $http_status_code = 400;
                 // No hay redirección aquí, se enviará JSON (ver final del script)
            }
            // Añadimos break aquí por si el 'else' de acción no reconocida se ejecuta
            break; // Fin de case 'POST'

        // ... case 'GET', case 'PUT', case 'DELETE', default ... (sin cambios respecto a la versión anterior)
         case 'GET':
             $response = ["success" => false, "message" => "Funcionalidad GET no implementada."];
             $http_status_code = 501;
             break;
         case 'PUT':
             if ($idUsuario !== null) { $data = json_decode(file_get_contents("php://input"), true); /* ... validación JSON ... */ $response = $usuarioController->actualizarUsuario($idUsuario, $data); /* ... ajustar $http_status_code ... */ }
             else { $response = ["success" => false, "message" => "Se requiere idUsuario para PUT"]; $http_status_code = 400; }
             break;
         case 'DELETE':
              if ($idUsuario !== null) { $response = $usuarioController->eliminarUsuario($idUsuario); /* ... ajustar $http_status_code ... */ }
              else { $response = ["success" => false, "message" => "Se requiere idUsuario para DELETE"]; $http_status_code = 400; }
             break;
         default:
              $response = ["success" => false, "message" => "Método HTTP no soportado."];
              $http_status_code = 405;
              break;

    } // Fin switch

} catch (Exception $e) {
    error_log("Error general en usuarioRoutes: " . $e->getMessage() . " en " . $e->getFile() . ":" . $e->getLine());
    $response = ["success" => false, 'message' => 'Ocurrió un error interno en el servidor.'];
    $http_status_code = 500;
}

// --- Enviar Respuesta Final JSON ---
// Solo se ejecuta si no hubo un exit() por redirección o error fatal anterior
http_response_code($http_status_code);
echo json_encode($response);

?>