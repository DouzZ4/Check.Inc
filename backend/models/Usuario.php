<?php
require_once '../config/database.php';

class Usuario {
    private $conn;
    private $table_name = "usuario";

    // Propiedades privadas
    private $idUsuario;
    private $user;
    private $password;
    private $documento;
    private $nombres;
    private $apellidos;
    private $correo;
    private $edad;
    private $idRol;

    // Constructor para inicializar la conexión y opcionalmente los datos
    public function __construct($db = null) {
        $conexion = new Conexion();
        $this->conn = $db ?: $conexion->conectar();
    }

    // Getters y setters para cada propiedad
    public function getIdUsuario() {
        return $this->idUsuario;
    }

    public function setIdUsuario($idUsuario) {
        $this->idUsuario = $idUsuario;
    }

    public function getUser() {
        return $this->user;
    }

    public function setUser($user) {
        $this->user = $user;
    }

    public function getPassword() {
        return $this->password;
    }

    public function setPassword($password) {
        $this->password = password_hash($password, PASSWORD_DEFAULT);
    }

    public function getDocumento() {
        return $this->documento;
    }

    public function setDocumento($documento) {
        $this->documento = $documento;
    }

    public function getNombres() {
        return $this->nombres;
    }

    public function setNombres($nombres) {
        $this->nombres = $nombres;
    }

    public function getApellidos() {
        return $this->apellidos;
    }

    public function setApellidos($apellidos) {
        $this->apellidos = $apellidos;
    }

    public function getCorreo() {
        return $this->correo;
    }

    public function setCorreo($correo) {
        $this->correo = $correo;
    }

    public function getEdad() {
        return $this->edad;
    }

    public function setEdad($edad) {
        $this->edad = $edad;
    }

    public function getIdRol() {
        return $this->idRol;
    }

    public function setIdRol($idRol) {
        $this->idRol = $idRol;
    }

    // Métodos (Ejemplo: Registrar usuario)
    public function registrar() {
        // Verificar si el usuario ya existe
        $sqlCheck = "SELECT idUsuario FROM usuario WHERE user = :user";
        $stmtCheck = $this->conn->prepare($sqlCheck);
        $stmtCheck->bindParam(':user', $this->user);
        $stmtCheck->execute();

        if ($stmtCheck->fetch()) {
            return ["message" => "❌ El nombre de usuario ya está en uso."];
        }

        // Insertar usuario
        $sql = "INSERT INTO usuario (user, password, documento, nombres, apellidos, correo, edad, idRol) 
                VALUES (:user, :password, :documento, :nombres, :apellidos, :correo, :edad, :idRol)";
        $stmt = $this->conn->prepare($sql);

        $stmt->bindParam(':user', $this->user);
        $stmt->bindParam(':password', $this->password);
        $stmt->bindParam(':documento', $this->documento);
        $stmt->bindParam(':nombres', $this->nombres);
        $stmt->bindParam(':apellidos', $this->apellidos);
        $stmt->bindParam(':correo', $this->correo);
        $stmt->bindParam(':edad', $this->edad);
        $stmt->bindParam(':idRol', $this->idRol);

        if ($stmt->execute()) {
            return ["message" => "✅ Usuario registrado correctamente."];
        } else {
            return ["message" => "❌ Error al registrar usuario."];
        }
    }
    // 🔹 Obtener usuario por ID
    public function obtenerPorId($idUsuario) {
        $sql = "SELECT * FROM usuario WHERE idUsuario = :idUsuario";
        $stmt = $this->conn->prepare($sql);
        $stmt->bindParam(':idUsuario', $idUsuario);
        $stmt->execute();
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    // 🔹 Login del usuario
    public function login($user, $password) {
        $sql = "SELECT * FROM usuario WHERE user = :user";
        $stmt = $this->conn->prepare($sql);
        $stmt->bindParam(':user', $user);
        $stmt->execute();
        $usuario = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($usuario && password_verify($password, $usuario['password'])) {
            return [
                "message" => "✅ Inicio de sesión exitoso.",
                "usuario" => [
                    "idUsuario" => $usuario['idUsuario'],
                    "user" => $usuario['user'],
                    "nombres" => $usuario['nombres'],
                    "apellidos" => $usuario['apellidos'],
                    "correo" => $usuario['correo'],
                    "edad" => $usuario['edad'],
                    "idRol" => $usuario['idRol']
                ]
            ];
        } else {
            return ["message" => "❌ Usuario o contraseña incorrectos."];
        }
    }
}
?>
}
?>