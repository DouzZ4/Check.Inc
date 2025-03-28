<?php
require_once '../controllers/UsuarioController.php';

// Inicializar el controlador
$usuarioController = new UsuarioController();

// Establecer encabezados comunes para las respuestas JSON
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// Verificar el método HTTP utilizado
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true) ?: $_POST; // Procesar JSON o datos del formulario

    // 🔹 Registrar usuario
    if (isset($_GET['register'])) {
        $usuarioController->registrarUsuario($data);
        exit; // 🔥 Detener la ejecución tras la respuesta
    }

    // 🔹 Login de usuario
    if (isset($_GET['login'])) {
        $usuarioController->loginUsuario($data);
        exit;
    }

    // Si no se especifica una acción válida
    echo json_encode(["message" => "❌ Acción no especificada."]);
    exit;
}

// Si se utiliza un método no permitido (ejemplo: GET para acciones POST)
http_response_code(405); // Código 405: Método no permitido
echo json_encode(["message" => "❌ Método no permitido. Usa POST para este endpoint."]);
?>