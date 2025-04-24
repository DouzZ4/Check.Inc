<?php

// Iniciar o reanudar la sesión
session_start();

// Limpiar todas las variables de sesión
$_SESSION = array();

// Expirar la cookie de sesión en el navegador
if (ini_get("session.use_cookies")) {
    $params = session_get_cookie_params();
    setcookie(session_name(), '', time() - 42000, 
        $params["path"], $params["domain"],
        $params["secure"], $params["httponly"]
    );
}

// Destruye la sesión en el servidor
session_destroy();

// Añade cabeceras anti-caché
header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
header("Pragma: no-cache");
header("Expires: Sat, 01 Jan 2000 00:00:00 GMT"); // Fecha en el pasado

// Redirigir a la página de inicio (o login)
$baseUrl = '/check.inc';
header('Location: ' . $baseUrl . '/backend/public/index.php');
exit; // Detener ejecución
?>