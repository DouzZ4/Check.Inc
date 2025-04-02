<?php
// Inicia la sesión para poder usar variables de sesión en todo el script.
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Incluye el controlador de Usuario, el modelo de Usuario y la configuración de la base de datos.
require_once __DIR__ . '/../controllers/UsuarioController.php';
require_once __DIR__ . '/../models/Usuario.php';
require_once __DIR__ . '/../config/database.php';

// Crea una instancia de la conexión a la base de datos.
$conexion = new Conexion();

// Crea una instancia del controlador de Usuario, pasando la conexión a la base de datos.
$usuarioController = new UsuarioController($conexion->conectar());

// Maneja las acciones específicas basadas en la solicitud
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = $_POST;

    if (isset($_GET['login'])) {
        $username = $data['username'] ?? '';
        $password = $data['password'] ?? '';

        if (empty($username) || empty($password)) {
            $_SESSION['message'] = "Por favor, ingrese usuario y contraseña.";
            header('Location: ../public/login.php');
            exit;
        }

        // Validar usuario en la base de datos
        $usuarioModel = new UsuarioModel($conexion->conectar());
        $usuario = $usuarioModel->obtenerUsuarioPorUsername($username);

        if ($usuario && password_verify($password, $usuario['password'])) {
            $_SESSION['idUsuario'] = $usuario['idUsuario'];
            $_SESSION['username'] = $usuario['username'];

            header('Location: ../public/index.php');
        } else {
            $_SESSION['message'] = "Usuario o contraseña incorrectos.";
            header('Location: ../public/login.php');
        }
        exit;
    }

    if (isset($_GET['register'])) {
        $resultado = $usuarioController->registrarUsuario($data);

        if ($resultado["success"]) {
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/login.php");
        } else {
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/registroUsuario.php");
        }
        exit;
    }

    $_SESSION['message'] = "❌ Acción no especificada.";
    header("Location: ../public/index.php");
    exit;

} else {
    http_response_code(405);
    echo "❌ Método no permitido. Usa POST para este endpoint.";
    exit;
}