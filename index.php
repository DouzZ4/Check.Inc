<?php

require_once __DIR__ . '/controllers/UsuarioController.php';

$controller = new UsuarioController();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $controller->registrarUsuario();
} else {
    $controller->mostrarFormularioRegistro();
}
