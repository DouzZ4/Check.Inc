<?php
require_once '../controllers/usuarioController.php';

// Inicializar el controlador
$usuarioController = new UsuarioController();

// Establecer encabezados comunes
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// Verificar método y parámetros para ejecutar la lógica correspondiente
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    if (isset($_GET['register'])) {
        // Llamar al método para registrar usuario
        $usuarioController->registrarUsuario($data);
    } elseif (isset($_GET['login'])) {
        // Llamar al método para inicio de sesión
        $usuarioController->loginUsuario($data);
    } else {
        // Respuesta para solicitudes POST sin acción especificada
        echo json_encode(["message" => "❌ Acción no especificada."]);
    }
    exit; // 🔥 Detener la ejecución después de manejar la solicitud
}

// Si se utiliza un método no permitido
http_response_code(405); // Código 405: Método no permitido
echo json_encode(["message" => "❌ Método no permitido. Usa POST para este endpoint."]);