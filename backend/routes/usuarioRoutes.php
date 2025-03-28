<?php
session_start();
require_once '../controllers/UsuarioController.php';
require_once '../config/database.php';

$conexion = new Conexion();
$usuarioController = new UsuarioController($conexion->conectar());

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Capturar datos enviados por el formulario
    $data = $_POST;

    // Registro de usuario
    if (isset($_GET['register'])) {
        $resultado = $usuarioController->registrarUsuario($data);
        if ($resultado["success"]) {
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/login.php"); // Si es exitoso, redirige a login
        } else {
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/registroUsuario.php"); // Si falla, regresa al registro
        }
        exit;
    }

    // Login de usuario
    if (isset($_GET['login'])) {
        $resultado = $usuarioController->loginUsuario($data);
        if ($resultado["success"]) {
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/dashboard.php"); // Aquí podrías redirigir a un panel o dashboard
        } else {
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/login.php");
        }
        exit;
    }

    $_SESSION['message'] = "❌ Acción no especificada.";
    header("Location: ../public/registroUsuario.php");
    exit;
} else {
    http_response_code(405);
    echo "❌ Método no permitido. Usa POST para este endpoint.";
    exit;
}
?>