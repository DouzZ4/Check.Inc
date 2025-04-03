<?php
// models/Usuario.php

// --- Clase UsuarioAbstracto (sin cambios, pero podrías añadir getters abstractos si quisieras) ---
abstract class UsuarioAbstracto {
    protected $nombres;
    protected $apellidos;
    protected $edad;
    protected $user;
    protected $documento;
    protected $password; // Guardará la contraseña en PLAINTEXT temporalmente antes de hashear

    public function __construct($nombres, $apellidos, $edad, $user, $documento, $password) {
        $this->nombres   = $nombres;
        $this->apellidos = $apellidos;
        $this->edad      = $edad;
        $this->user      = $user;
        $this->documento = $documento;
        $this->password  = $password; // Se recibe la contraseña sin hashear
    }

    // Getters para propiedades comunes (¡NUEVO!)
    public function getNombres() { return $this->nombres; }
    public function getApellidos() { return $this->apellidos; }
    public function getEdad() { return $this->edad; }
    public function getUser() { return $this->user; }
    public function getDocumento() { return $this->documento; }
    public function getPassword() { return $this->password; } // Devuelve la contraseña (sin hashear si se acaba de crear)

    abstract public function validarUsuario();
    abstract public function validarPassword();
}

// --- Clase Usuario (Entidad - con Getters) ---
class Usuario extends UsuarioAbstracto {
    private $correo;
    private $idRol;
    // Podríamos añadir idUsuario si lo recuperamos de la BD
    // private $idUsuario;

    public function __construct($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol) {
        parent::__construct($nombres, $apellidos, $edad, $user, $documento, $password);
        $this->correo = $correo; // Validar correo aquí o con setter?
        $this->idRol = $idRol; // Validar rol?
    }

    // Getters para propiedades específicas (¡NUEVO!)
    public function getCorreo() { return $this->correo; }
    public function getIdRol() { return $this->idRol; }

    // Métodos de validación (sin cambios)
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

     // Podríamos añadir setters si necesitáramos modificar post-creación
     // public function setCorreo($correo) { /* con validación */ $this->correo = $correo; }
}

// --- Clase UsuarioModel (Corregida y Mejorada) ---
class UsuarioModel {
    private $conn;

    public function __construct($dbConnection) {
        $this->conn = $dbConnection;
        $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }

    public function obtenerUsuarioPorUser($user): ?array { // Cambiado nombre método por claridad
        try {
            $sql = "SELECT * FROM usuario WHERE user = :user"; // Asumiendo columna se llama 'user'
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':user', $user);
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            return $result ?: null; // Devuelve array o null
        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::obtenerUsuarioPorUser: " . $e->getMessage());
            return null;
        }
    }
    public function obtenerUsuarioPorId(int $idUsuario): ?array { // Cambiado nombre método por claridad
        $sql = "SELECT * FROM usuario WHERE idUsuario = :idUsuario"; // Asumiendo columna se llama 'idUsuario'
        try {
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            return $result ?: null; // Devuelve array o null
        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::obtenerUsuarioPorId: " . $e->getMessage());
            return null;
        }
    }

    /**
     * Registra un nuevo usuario, hasheando la contraseña.
     * @param Usuario $usuario Objeto entidad con datos (contraseña SIN hashear).
     * @return bool True si la inserción fue exitosa, False si no.
     */
    public function registrarUsuario(Usuario $usuario): bool {
        // --- ¡CORRECCIÓN IMPORTANTE! Hashing de contraseña ---
        $hashedPassword = password_hash($usuario->getPassword(), PASSWORD_BCRYPT);
        if ($hashedPassword === false) {
             error_log("Error al hashear la contraseña para el usuario: " . $usuario->getUser());
             return false; // Error en el hash
        }

        // --- ¡CORRECCIÓN IMPORTANTE! Nombre columna idRol ---
        // Asumimos que la columna en la BD se llama 'idRol' (como en el controller original)
        $sql = "INSERT INTO usuario (nombres, apellidos, edad, correo, user, documento, password, idRol)
                VALUES (:nombres, :apellidos, :edad, :correo, :user, :documento, :password, :idRol)";
        try {
            $stmt = $this->conn->prepare($sql);

            // --- Usando Getters ---
            $stmt->bindValue(':nombres', $usuario->getNombres()); // Usar bindValue es a menudo más simple
            $stmt->bindValue(':apellidos', $usuario->getApellidos());
            $stmt->bindValue(':edad', $usuario->getEdad());
            $stmt->bindValue(':correo', $usuario->getCorreo());
            $stmt->bindValue(':user', $usuario->getUser());
            $stmt->bindValue(':documento', $usuario->getDocumento());
            $stmt->bindValue(':password', $hashedPassword); // <- Guardar la contraseña HASHEADA
            $stmt->bindValue(':idRol', $usuario->getIdRol(), PDO::PARAM_INT); // <- Usar idRol y especificar tipo

            return $stmt->execute(); // execute() devuelve true/false

        } catch (PDOException $e) {
            // Podríamos verificar códigos de error específicos, ej. 23000 para duplicados
            error_log("Error en UsuarioModel::registrarUsuario: " . $e->getMessage());
            return false;
        }
    }
 // --- NUEVO: Método para Actualizar Usuario ---
    /**
     * Actualiza datos específicos de un usuario existente (excluye contraseña, user, idRol).
     * @param int $idUsuario ID del usuario a actualizar.
     * @param array $data Array asociativo con datos validados a actualizar (ej. ['nombres' => 'Juan', 'correo' => 'j@c.com']).
     * @return bool True si se actualizó al menos una fila, False en caso contrario o error.
     */
    public function update(int $idUsuario, array $data): bool {
        // Campos permitidos para actualizar en este método genérico
        $allowedFields = ['nombres', 'apellidos', 'edad', 'correo', 'documento']; // Excluye 'user', 'password', 'idRol'
        $fieldsToUpdate = array_intersect_key($data, array_flip($allowedFields));

        if (empty($fieldsToUpdate)) {
            return false; // No hay campos válidos o permitidos para actualizar
        }

        $setParts = [];
        $params = [':idUsuario' => $idUsuario];

        foreach ($fieldsToUpdate as $columna => $valor) {
            $placeholder = ':' . $columna;
            $setParts[] = "`" . $columna . "` = " . $placeholder; // Asume nombres de columna iguales a las claves
            $params[$placeholder] = $valor;
        }

        $sql = "UPDATE usuario SET " . implode(', ', $setParts) . " WHERE idUsuario = :idUsuario"; // Asume PK es idUsuario

        try {
            $stmt = $this->conn->prepare($sql);

            // Bind todos los parámetros
            foreach ($params as $placeholder => $valor) {
                $tipo = ($placeholder === ':idUsuario' || $placeholder === ':edad') ? PDO::PARAM_INT : PDO::PARAM_STR; // Ajusta tipos si es necesario
                $stmt->bindValue($placeholder, $valor, $tipo);
            }

            $stmt->execute();
            return $stmt->rowCount() > 0; // True si se afectaron filas

        } catch (PDOException $e) {
            error_log("Error en UsuarioModel::update: " . $e->getMessage());
            return false;
        }
    }

    // --- NUEVO: Método para Eliminar Usuario ---
    /**
     * Elimina un usuario por su ID.
     * @param int $idUsuario ID del usuario a eliminar.
     * @return bool True si se eliminó al menos una fila, False en caso contrario o error.
     */
    public function delete(int $idUsuario): bool {
        // ¡Consideración! ¿Qué pasa con los registros de glucosa asociados?
        // Podrías necesitar configurar ON DELETE CASCADE en la BD o eliminarlos manualmente antes.
        $sql = "DELETE FROM usuario WHERE idUsuario = :idUsuario"; // Asume PK es idUsuario
        try {
            $stmt = $this->conn->prepare($sql);
            $stmt->bindParam(':idUsuario', $idUsuario, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->rowCount() > 0; // True si se afectaron filas
        } catch (PDOException $e) {
             // Podría fallar por restricciones de clave foránea si hay registros asociados
             error_log("Error en UsuarioModel::delete: " . $e->getMessage());
             return false;
        }
    }

     // --- Podríamos añadir otros métodos aquí ---
     // findById($id), update($usuario), delete($id), etc.
}

// --- Clase UsuarioFactory (sin cambios necesarios) ---
class UsuarioFactory {
    public static function crearUsuario($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol) {
        // Pasar la contraseña tal cual, el Modelo se encargará de hashearla al guardar
        return new Usuario($nombres, $apellidos, $edad, $correo, $user, $documento, $password, $idRol);
    }
}
?>