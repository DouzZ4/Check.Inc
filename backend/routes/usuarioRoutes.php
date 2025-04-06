<?php
// routes/usuarioRoutes.php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL); // Mantenlo para desarrollo, quítalo en producción

// --- Cabeceras ---
header("Access-Control-Allow-Origin: *");
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
    http_response_code(503);
    echo json_encode(["success" => false, "message" => "Error de conexión DB: " . $e->getMessage()]);
    exit;
}

// --- Inyección de Dependencias ---
try {
    $usuarioModel = new UsuarioModel($conn);
    $usuarioController = new UsuarioController($usuarioModel);
} catch(Exception $e) {
     http_response_code(500);
     echo json_encode(["success" => false, "message" => "Error inicializando componentes: " . $e->getMessage()]);
     exit;
}

// --- Enrutamiento Principal ---
$method = $_SERVER['REQUEST_METHOD'];
$response = ["success" => false, "message" => "Solicitud no válida."];
$http_status_code = 400;

// --- Simplificar Detección de Acción ---
// Usar el parámetro 'action' para POST, o el parámetro específico para otros métodos
$action = $_GET['action'] ?? null; // Para POST: ?action=login o ?action=register
$idUsuario = isset($_GET['idUsuario']) && is_numeric($_GET['idUsuario']) ? (int)$_GET['idUsuario'] : null; // Para GET(id), PUT, DELETE

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
                $response = $usuarioController->loginUsuario($data);
                // Iniciar sesión y redirigir (SIEMPRE redirige en login, éxito o fallo)
                session_start();
                if ($response['success']) {
                    // Controller ya guardó sesión, redirigir a página principal
                    header('Location: ../public/index.php'); // Ajusta a tu página principal post-login
                    exit();
                } else {
                    // Falló login, redirigir de vuelta a login con mensaje
                    $_SESSION['message'] = $response['message'];
                    $_SESSION['message_type'] = 'error';
                    header('Location: ../public/login.php');
                    exit();
                }

            } elseif ($action === 'register') {
                // Llamar al controlador para registrar
                $response = $usuarioController->registrarUsuario($data);
                // Iniciar sesión y redirigir (SIEMPRE redirige en registro, éxito o fallo)
                session_start();
                $_SESSION['message'] = $response['message']; // Guardar mensaje

                if ($response['success']) {
                    // Éxito registro -> Redirigir a login
                    $_SESSION['message_type'] = 'success';
                    header('Location: ../public/login.php');
                    exit();
                } else {
                    // Fallo registro -> Redirigir DE VUELTA a registro
                    $_SESSION['message_type'] = 'error';
                    // Considera guardar los datos $_POST (menos password) en sesión para repoblar el form
                    header('Location: ../public/registrousuario.php');
                    exit();
                }

            } else {
                 // Si action no es 'login' ni 'register'
                 $response = ["success" => false, "message" => "Acción POST no reconocida. Falta ?action=login o ?action=register"];
                 $http_status_code = 400;
                 // No hay redirección aquí, se enviará JSON
            }
            // No se necesita break aquí porque todos los caminos válidos usan exit()
            break; // Break de seguridad al final de case POST

        // ... case 'GET', case 'PUT', case 'DELETE', default ...
        // (El código para GET, PUT, DELETE que tenías antes está bien,
        // asumiendo que esas acciones SÍ esperan/devuelven JSON usualmente)

        case 'GET':
             $response = ["success" => false, "message" => "Funcionalidad GET no implementada."];
             $http_status_code = 501;
             break;
         case 'PUT':
             if ($idUsuario !== null) { $data = json_decode(file_get_contents("php://input"), true); /* ... */ $response = $usuarioController->actualizarUsuario($idUsuario, $data); /* ... */ }
             else { /* Error falta id */ }
             break;
         case 'DELETE':
              if ($idUsuario !== null) { $response = $usuarioController->eliminarUsuario($idUsuario); /* ... */ }
              else { /* Error falta id */ }
             break;
         default:
              $response = ["success" => false, "message" => "Método HTTP no soportado."];
              $http_status_code = 405;
              break;

    } // Fin switch

} catch (Exception $e) {
    error_log("Error general en usuarioRoutes: " . $e->getMessage() . " en " . $e->getFile() . ":" . $e->getLine());
    $response = ['status' => 'error', 'message' => 'Ocurrió un error interno en el servidor.']; // Cambiado 'status' a 'success' para consistencia
    $response['success'] = false; // Asegurar success=false
    $http_status_code = 500;
    // Para excepciones, sí devolvemos JSON
    // header('Location: pagina_error_generico.php'); // O redirigir a página de error
    // exit();
}

// --- Enviar Respuesta Final JSON ---
// Esta parte solo se ejecutará si NO hubo una redirección con exit() antes
// (ej. para PUT, DELETE, GET, o errores POST no reconocidos, o excepciones si no rediriges)
http_response_code($http_status_code);
echo json_encode($response);

?>