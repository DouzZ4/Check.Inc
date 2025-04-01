<?php
// models/Usuario.php

// Clase abstracta que define la estructura básica para un Usuario.
// Ahora se incluyen los campos: nombres, apellidos y edad.
abstract class UsuarioAbstracto {
    protected $nombres;
    protected $apellidos;
    protected $edad;
    protected $username;
    protected $documento;
    protected $password;

    // Se actualiza el constructor para recibir 5 parámetros.
    public function __construct($nombres, $apellidos, $edad, $username, $documento, $password) {
        $this->nombres   = $nombres;
        $this->apellidos = $apellidos;
        $this->edad      = $edad;
        $this->username = $username;
        $this->documento = $documento;
        $this->password = $password;
    }

    // Método para validar el nombre de usuario (se implementa en la clase hija).
    abstract public function validarUsuario();

    // Método para validar la contraseña (se implementa en la clase hija).
    abstract public function validarPassword();
}

// Clase concreta que extiende de UsuarioAbstracto e incluye propiedades adicionales.
class Usuario extends UsuarioAbstracto {
    private $correo;
    private $id_rol;

    // Se actualiza el constructor para recibir 8 parámetros:
    // $nombres, $apellidos, $edad, $correo, $direccion, $username, $password, $id_rol
    public function __construct($nombres, $apellidos, $edad, $correo, $username, $documento, $password, $id_rol) {
        parent::__construct($nombres, $apellidos, $edad, $username, $documento, $password);
        $this->correo    = $correo;
        $this->id_rol    = $id_rol;
    }

    // Validación del nombre de usuario: deben ser al menos 5 caracteres.
    public function validarUsuario() {
        return strlen($this->username) >= 5 ? true : "El nombre de usuario debe tener al menos 5 caracteres.";
    }

    // Validación de la contraseña: al menos 8 caracteres, incluir mayúsculas, minúsculas y un carácter especial.
    public function validarPassword(): bool|array {
        $errores = [];

        // Verificar que la contraseña tenga al menos 8 caracteres.
        if (strlen($this->password) < 8) {
            $errores[] = "La contraseña debe tener al menos 8 caracteres.";
        }

        // Verificar la presencia de letras minúsculas y mayúsculas.
        if (!preg_match('/[a-z]/', $this->password) || !preg_match('/[A-Z]/', $this->password)) {
            $errores[] = "La contraseña debe incluir mayúsculas y minúsculas.";
        }

        // Verificar la presencia de al menos un carácter especial.
        if (!preg_match('/[\W_]/', $this->password)) {
            $errores[] = "La contraseña debe incluir al menos un carácter especial.";
        }

        // Siempre se retorna un valor: true si no hay errores o un arreglo de errores si los hay.
        return empty($errores) ? true : $errores;
    }
}

// Fábrica que facilita la creación de objetos Usuario.
// Ahora acepta 8 parámetros en total.
class UsuarioFactory {
    public static function crearUsuario($nombres, $apellidos, $edad, $correo, $username, $documento, $password, $id_rol) {
        return new Usuario($nombres, $apellidos, $edad, $correo,  $username,$documento, $password, $id_rol);
    }
}
?>