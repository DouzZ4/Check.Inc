<?php
// controllers/UsuarioController.php

require_once '../models/Usuario.php';

class UsuarioController {
    private $conexion;

    // Constructor que recibe la conexión a la base de datos.
    public function __construct($conexion) {
        $this->conexion = $conexion;
    }

    // Método para registrar un usuario (paciente).
    public function registrarUsuario($data) {
        // Forzamos el rol a paciente (valor 2).
        $data['idRol'] = 2;

        /* * Actualizamos la llamada a la fábrica.
         * La fábrica espera 8 parámetros (según nuestro modelo actualizado), en el siguiente orden:
         * ($nombres, $apellidos, $edad, $correo, $direccion, $user, $password, $id_rol)
         */
        $usuario = UsuarioFactory::crearUsuario(
            $data['nombres'],
            $data['apellidos'],
            $data['edad'],
            $data['correo'],
            $data['user'],
            $data['documento'],
            $data['password'],
            $data['idRol']
        );

        // Validar el nombre de usuario.
        $validacionUsuario = $usuario->validarUsuario();
        if ($validacionUsuario !== true) {
            return ["success" => false, "message" => $validacionUsuario];
        }

        // Validar la contraseña.
        $validacionPassword = $usuario->validarPassword();
        if ($validacionPassword !== true) {
            $mensajeErrors = is_array($validacionPassword) ? implode(' ', $validacionPassword) : $validacionPassword;
            return ["success" => false, "message" => $mensajeErrors];
        }

        // Insertar el usuario en la base de datos.
        $sql = "INSERT INTO usuario (nombres, apellidos, edad, correo, user,documento, password, idRol) 
                VALUES (:nombres, :apellidos, :edad, :correo, :user,:documento, :password, :idRol)";
        $stmt = $this->conexion->prepare($sql);

        $stmt->bindParam(':nombres', $data['nombres']);
        $stmt->bindParam(':apellidos', $data['apellidos']);
        $stmt->bindParam(':edad', $data['edad']);
        $stmt->bindParam(':correo', $data['correo']);
        $stmt->bindParam(':user', $data['user']); // Se vincula al campo 'user'
        $stmt->bindParam(':documento', $data['documento']); // Se vincula al campo 'documento'
        $hashedPassword = password_hash($data['password'], PASSWORD_BCRYPT);
        $stmt->bindParam(':password', $hashedPassword);
        $stmt->bindParam(':idRol', $data['idRol']);

        if ($stmt->execute()) {
            return ["success" => true, "message" => "✅ Usuario registrado correctamente."];
        } else {
            return ["success" => false, "message" => "❌ Error al registrar usuario."];
        }
    }

    // Método para el inicio de sesión.
    public function loginUsuario($data) {
        if (isset($data['user']) && isset($data['password'])) {
            // Se consulta la columna 'user', ya que en la bd el campo se llama así.
            $sql = "SELECT * FROM usuario WHERE user = :user";
            $stmt = $this->conexion->prepare($sql);
            $stmt->bindParam(':user', $data['user']);
            $stmt->execute();
            $usuario = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($usuario && password_verify($data['password'], $usuario['password'])) {
                return ["success" => true, "message" => "✅ Inicio de sesión exitoso."];
            } else {
                return ["success" => false, "message" => "❌ Credenciales incorrectas."];
            }
        } else {
            return ["success" => false, "message" => "❌ Datos incompletos."];
        }
    }
}
?>