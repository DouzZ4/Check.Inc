<?php
// Inicia la sesión para poder usar variables de sesión en todo el script.
session_start();

// Incluye el controlador de Usuario y la configuración de la base de datos.
require_once '../controllers/UsuarioController.php';
require_once '../config/database.php';

// Crea una instancia de la conexión a la base de datos.
$conexion = new Conexion();

// Crea una instancia del controlador de Usuario, pasando la conexión a la base de datos.
$usuarioController = new UsuarioController($conexion->conectar());

// Verifica si la petición es de tipo POST. Solo procesaremos peticiones POST.
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Captura todos los datos enviados por el formulario en la variable $data.
    $data = $_POST;

    // Verifica si la petición es para registrar un usuario.
    if (isset($_GET['register'])) {
        // Llama al método del controlador para registrar al usuario.
        $resultado = $usuarioController->registrarUsuario($data);

        // Verifica si el registro fue exitoso.
        if ($resultado["success"]) {
            // Si es exitoso, almacena el mensaje en la sesión y redirige al login.
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/login.php");
        } else {
            // Si falla, almacena el mensaje de error en la sesión y redirige de vuelta al registro.
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/registroUsuario.php");
        }
        // Termina la ejecución del script después de la redirección.
        exit;
    }

    // Verifica si la petición es para el login de un usuario.
    if (isset($_GET['login'])) {
        // Llama al método del controlador para realizar el login.
        $resultado = $usuarioController->loginUsuario($data);

        // Verifica si el login fue exitoso.
        if ($resultado["success"]) {
            // Si es exitoso, almacena el mensaje en la sesión y redirige al dashboard.
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/dashboard.php");
        } else {
            // Si falla, almacena el mensaje de error en la sesión y redirige de vuelta al login.
            $_SESSION['message'] = $resultado["message"];
            header("Location: ../public/login.php");
        }
        // Termina la ejecución del script después de la redirección.
        exit;
    }

    // Si no se especificó la acción (register o login), muestra un mensaje de error y redirige al registro.
    $_SESSION['message'] = "❌ Acción no especificada.";
    header("Location: ../public/registroUsuario.php");
    exit;
} else {
    // Si la petición no es POST, envía un código de error 405 (Método no permitido) y un mensaje.
    http_response_code(405);
    echo "❌ Método no permitido. Usa POST para este endpoint.";
    exit;
}
?>