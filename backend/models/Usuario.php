<?php
// models/Usuario.php

// Clase abstracta que define la estructura básica para un Usuario.
abstract class UsuarioAbstracto {
    protected $nombres;
    protected $apellidos;
    protected $edad;
    protected $user;
    protected $documento;
    protected $password;

    // Constructor para inicializar las propiedades comunes del usuario.
    public function __construct($nombres, $apellidos, $edad, $user, $documento, $password) {
        $this->nombres   = $nombres;
        $this->apellidos = $apellidos;
        $this->edad      = $edad;
        $this->user  = $user;
        $this->documento = $documento;
        $this->password  = $password;
    }

    // Método para validar el nombre de usuario (debe implementarse en la clase concreta).
    abstract public function validarUsuario();

    // Método para validar la contraseña (debe implementarse en la clase concreta).
    abstract public function validarPassword();
}

// Clase concreta que extiende de UsuarioAbstracto y agrega propiedades adicionales.
class Usuario extends UsuarioAbstracto {
    private $correo;
    private $idRol;

    // Constructor que inicializa las propiedades adicionales específicas de Usuario.
    public function __construct($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol) {
        parent::__construct($nombres, $apellidos, $edad, $user, $documento, $password);
        $this->correo = $correo;
        $this->idRol = $idRol;
    }

    // Validación del nombre de usuario: al menos 5 caracteres.
    public function validarUsuario() {
        return strlen($this->user) >= 5 ? true : "El nombre de usuario debe tener al menos 5 caracteres.";
    }

    // Validación de la contraseña: incluye longitud, mayúsculas, minúsculas y caracteres especiales.
    public function validarPassword() {
        $errores = [];

        // Verifica que la contraseña tenga al menos 8 caracteres.
        if (strlen($this->password) < 8) {
            $errores[] = "La contraseña debe tener al menos 8 caracteres.";
        }

        // Verifica que contenga letras mayúsculas y minúsculas.
        if (!preg_match('/[a-z]/', $this->password) || !preg_match('/[A-Z]/', $this->password)) {
            $errores[] = "La contraseña debe incluir mayúsculas y minúsculas.";
        }

        // Verifica que contenga al menos un carácter especial.
        if (!preg_match('/[\W_]/', $this->password)) {
            $errores[] = "La contraseña debe incluir al menos un carácter especial.";
        }

        // Devuelve true si no hay errores, o un arreglo con los errores encontrados.
        return empty($errores) ? true : $errores;
    }
}

// Modelo para interactuar con la base de datos.
class UsuarioModel {
    private $conn;

    // Constructor que establece la conexión a la base de datos.
    public function __construct($dbConnection) {
        $this->conn = $dbConnection;
    }

    // Método para obtener un usuario por su nombre de usuario.
    public function obtenerUsuarioPoruser($user) {
        try {
            $sql = "SELECT * FROM usuario WHERE user = :user";
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':user', $user);
            $stmt->execute();
            return $stmt->fetch(PDO::FETCH_ASSOC); // Devuelve los datos del usuario como un array asociativo.
        } catch (PDOException $e) {
            return null; // Devuelve null si ocurre un error.
        }
    }

    // Método para registrar un nuevo usuario en la base de datos.
    public function registrarUsuario($usuario) {
        try {
            $sql = "INSERT INTO usuario (nombres, apellidos, edad, correo, user, documento, password, id_rol)
                    VALUES (:nombres, :apellidos, :edad, :correo, :user, :documento, :password, :id_rol)";
            $stmt = $this->conn->prepare($sql);

            $stmt->bindParam(':nombres', $usuario->nombres);
            $stmt->bindParam(':apellidos', $usuario->apellidos);
            $stmt->bindParam(':edad', $usuario->edad);
            $stmt->bindParam(':correo', $usuario->correo);
            $stmt->bindParam(':user', $usuario->user);
            $stmt->bindParam(':documento', $usuario->documento);
            $stmt->bindParam(':password', $usuario->password);
            $stmt->bindParam(':id_rol', $usuario->idRol);

            return $stmt->execute(); // Devuelve true si la inserción fue exitosa.
        } catch (PDOException $e) {
            return false; // Devuelve false si ocurre un error.
        }
    }
}

// Fábrica para crear instancias de Usuario.
class UsuarioFactory {
    public static function crearUsuario($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol) {
        return new Usuario($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol);
    }
}
?>