<?php
// models/Usuario.php

abstract class UsuarioAbstracto {
    protected $nombres;
    protected $apellidos;
    protected $edad;
    protected $user;
    protected $documento;
    protected $password;

    public function __construct($nombres, $apellidos, $edad, $user, $documento, $password) {
        $this->nombres   = $nombres;
        $this->apellidos = $apellidos;
        $this->edad      = $edad;
        $this->user      = $user;
        $this->documento = $documento;
        $this->password  = $password;
    }

    public function getNombres() { return $this->nombres; }
    public function getApellidos() { return $this->apellidos; }
    public function getEdad() { return $this->edad; }
    public function getUser() { return $this->user; }
    public function getDocumento() { return $this->documento; }
    public function getPassword() { return $this->password; }

    abstract public function validarUsuario();
    abstract public function validarPassword();
}

class Usuario extends UsuarioAbstracto {
    private $correo;
    private $idRol;

    public function __construct($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol) {
        parent::__construct($nombres, $apellidos, $edad, $user, $documento, $password);
        $this->correo = $correo;
        $this->idRol = $idRol;
    }

    public function getCorreo() { return $this->correo; }
    public function getIdRol() { return $this->idRol; }

    public function validarUsuario() {
        return strlen($this->user) >= 5 ? true : "El nombre de usuario debe tener al menos 5 caracteres.";
    }

    public function validarPassword() {
        $errores = [];
        if (strlen($this->password) < 8) { $errores[] = "La contraseña debe tener al menos 8 caracteres."; }
        if (!preg_match('/[a-z]/', $this->password) || !preg_match('/[A-Z]/', $this->password)) { $errores[] = "La contraseña debe incluir mayúsculas y minúsculas."; }
        if (!preg_match('/[\W_]/', $this->password)) { $errores[] = "La contraseña debe incluir al menos un carácter especial."; }
        return empty($errores) ? true : $errores;
    }
}

class UsuarioModel {
    private $conn;

    public function __construct($dbConnection) {
        $this->conn = $dbConnection;
        $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }

    public function obtenerUsuarioPorUser($user): ?array {
        try {
            $sql = "SELECT * FROM usuario WHERE user = :user";
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':user', $user);
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            return $result ?: null;
        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::obtenerUsuarioPorUser: " . $e->getMessage());
            return null;
        }
    }

    public function obtenerUsuarioPorId(int $idUsuario): ?array {
        $sql = "SELECT * FROM usuario WHERE idUsuario = :idUsuario";
        try {
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            return $result ?: null;
        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::obtenerUsuarioPorId: " . $e->getMessage());
            return null;
        }
    }

    public function obtenerUsuarioPorDocumento(string $documento): ?array {
        $sql = "SELECT idUsuario, documento FROM usuario WHERE documento = :documento";
        try {
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':documento', $documento);
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            return $result ?: null;
        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::obtenerUsuarioPorDocumento: " . $e->getMessage());
            return null;
        }
    }

    public function registrarUsuario(Usuario $usuario): bool {
        $hashedPassword = password_hash($usuario->getPassword(), PASSWORD_BCRYPT);
        if ($hashedPassword === false) {
            error_log("Error al hashear la contraseña para el usuario: " . $usuario->getUser());
            return false;
        }

        $sql = "INSERT INTO usuario (nombres, apellidos, edad, correo, user, documento, password, idRol)
                VALUES (:nombres, :apellidos, :edad, :correo, :user, :documento, :password, :idRol)";
        try {
            $stmt = $this->conn->prepare($sql);

            $stmt->bindValue(':nombres', $usuario->getNombres());
            $stmt->bindValue(':apellidos', $usuario->getApellidos());
            $stmt->bindValue(':edad', $usuario->getEdad());
            $stmt->bindValue(':correo', $usuario->getCorreo());
            $stmt->bindValue(':user', $usuario->getUser());
            $stmt->bindValue(':documento', $usuario->getDocumento());
            $stmt->bindValue(':password', $hashedPassword);
            $stmt->bindValue(':idRol', $usuario->getIdRol(), PDO::PARAM_INT);

            return $stmt->execute();

        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::registrarUsuario: " . $e->getMessage());
            return false;
        }
    }

    public function update(int $idUsuario, array $data): bool {
        $allowedFields = ['nombres', 'apellidos', 'edad', 'correo', 'documento'];
        $fieldsToUpdate = array_intersect_key($data, array_flip($allowedFields));

        if (empty($fieldsToUpdate)) {
            return false;
        }

        $setParts = [];
        $params = [':idUsuario' => $idUsuario];

        foreach ($fieldsToUpdate as $columna => $valor) {
            $placeholder = ':' . $columna;
            $setParts[] = "`" . $columna . "` = " . $placeholder;
            $params[$placeholder] = $valor;
        }

        $sql = "UPDATE usuario SET " . implode(', ', $setParts) . " WHERE idUsuario = :idUsuario";

        try {
            $stmt = $this->conn->prepare($sql);

            foreach ($params as $placeholder => $valor) {
                $tipo = ($placeholder === ':idUsuario' || $placeholder === ':edad') ? PDO::PARAM_INT : PDO::PARAM_STR;
                $stmt->bindValue($placeholder, $valor, $tipo);
            }

            $stmt->execute();
            return $stmt->rowCount() > 0;

        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::update: " . $e->getMessage());
            return false;
        }
    }

    public function delete(int $idUsuario): bool {
        $sql = "DELETE FROM usuario WHERE idUsuario = :idUsuario";
        try {
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->rowCount() > 0;
        } catch (PDOException $e) {
             error_log("Error en UsuarioModel::delete: " . $e->getMessage());
             return false;
        }
    }
}

class UsuarioFactory {
    public static function crearUsuario($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol) {
        return new Usuario($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol);
    }
}

?>