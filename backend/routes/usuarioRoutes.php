<?php
// Inicia la sesión si no está ya activa.
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Incluye dependencias necesarias.
require_once __DIR__ . '/../controllers/UsuarioController.php';
require_once __DIR__ . '/../models/Usuario.php';
require_once __DIR__ . '/../config/database.php';

// Crea una instancia de la conexión a la base de datos.
$conexion = new Conexion();

// Crea una instancia del controlador de Usuario, pasando la conexión.
$usuarioController = new UsuarioController($conexion->conectar());

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = $_POST;

    // **Login de Usuario**
    if (isset($_GET['login'])) {
        // Validar que se envíen las credenciales necesarias
        $user = $data['user'] ?? '';
        $password = $data['password'] ?? '';

        if (empty($user) || empty($password)) {
            $_SESSION['message'] = "Por favor, ingrese usuario y contraseña.";
            header('Location: ../public/login.php'); // Redirige al login
            exit;
        }

        // Validar usuario en la base de datos
        $usuarioModel = new UsuarioModel($conexion->conectar());
        $usuario = $usuarioModel->obtenerUsuarioPoruser($user);

        if ($usuario && password_verify($password, $usuario['password'])) {
            // Establece variables de sesión
            $_SESSION['idUsuario'] = $usuario['idUsuario'];
            $_SESSION['user'] = $usuario['user'];

            // Redirige al dashboard principal
            header('Location: ../public/index.php');
            exit;
        } else {
            $_SESSION['message'] = "Usuario o contraseña incorrectos.";
            header('Location: ../public/login.php');
            exit;
        }
    }

    // **Registro de Usuario**
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

    // Acción no especificada
    $_SESSION['message'] = "❌ Acción no especificada.";
    header("Location: ../public/index.php");
    exit;
}

// Si no es una solicitud POST, rechaza con un error.
http_response_code(405);
echo "❌ Método no permitido. Usa POST para este endpoint.";
exit;