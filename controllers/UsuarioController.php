<?php

require_once __DIR__ . '/../models/Usuario.php';

class UsuarioController {
    private $usuarioModel;

    public function __construct() {
        $this->usuarioModel = new Usuario();
    }

    public function mostrarFormularioRegistro() {
        // Obtener roles desde el modelo
        $roles = $this->usuarioModel->ObtenerRoles();
        require __DIR__ . '/../views/Usuarios/registro.php'; // Cargar la vista
    }

    public function registrarUsuario() {
        if ($_SERVER["REQUEST_METHOD"] == "POST") {
            $user = $_POST['user'];
            $password = $_POST['password'];
            $documento = $_POST['documento'];
            $nombres = $_POST['nombres'];
            $apellidos = $_POST['apellidos'];
            $correo = $_POST['correo'];
            $edad = $_POST['edad'];
            $idRol = $_POST['idRol'];

            if ($this->usuarioModel->registrarUsuario($user, $password, $documento, $nombres, $apellidos, $correo, $edad, $idRol)) {
                echo "<p>Usuario registrado exitosamente.</p>";
            } else {
                echo "<p>Error al registrar usuario.</p>";
            }
        }
    }
}
