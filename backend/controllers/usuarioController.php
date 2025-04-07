<?php
// controllers/UsuarioController.php

require_once '../models/Usuario.php';

class UsuarioController {
    private $usuarioModel;

    public function __construct(UsuarioModel $usuarioModel) {
        $this->usuarioModel = $usuarioModel;
    }

    public function registrarUsuario($data): array {
        try {
             $data['idRol'] = 2;
             if (empty($data['nombres']) || empty($data['apellidos']) || empty($data['edad']) || empty($data['correo']) || empty($data['user']) || empty($data['documento']) || empty($data['password'])) {
                 return ["success" => false, "message" => "❌ Todos los campos son requeridos."];
             }

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

             $validacionUsuario = $usuario->validarUsuario();
             if ($validacionUsuario !== true) {
                 return ["success" => false, "message" => $validacionUsuario];
             }
             $validacionPassword = $usuario->validarPassword();
             if ($validacionPassword !== true) {
                 $mensajeErrors = is_array($validacionPassword) ? implode(' ', $validacionPassword) : $validacionPassword;
                 return ["success" => false, "message" => $mensajeErrors];
             }

             if ($this->usuarioModel->obtenerUsuarioPorUser($usuario->getUser()) !== null) {
                 return ["success" => false, "message" => "❌ El nombre de usuario ya está en uso."];
             }

             if ($this->usuarioModel->obtenerUsuarioPorDocumento($usuario->getDocumento()) !== null) {
                 return ["success" => false, "message" => "❌ El número de documento ya está registrado."];
             }

             $registrado = $this->usuarioModel->registrarUsuario($usuario);

             if ($registrado) {
                 return ["success" => true, "message" => "✅ Usuario registrado correctamente."];
             } else {
                 return ["success" => false, "message" => "❌ Error del servidor al registrar usuario. Inténtalo más tarde."];
             }
         } catch (Exception $e) {
             error_log("Excepción en UsuarioController::registrarUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado."];
         }
    }

    public function loginUsuario($data): array {
        if (empty($data['user']) || empty($data['password'])) {
            return ["success" => false, "message" => "❌ Usuario y contraseña son requeridos."];
        }

        try {
            $usuarioData = $this->usuarioModel->obtenerUsuarioPorUser($data['user']);

            if ($usuarioData && isset($usuarioData['password']) && password_verify($data['password'], $usuarioData['password'])) {
                if (session_status() === PHP_SESSION_NONE) {
                    session_start();
                }
                $_SESSION['idUsuario'] = $usuarioData['idUsuario'];
                $_SESSION['nombreUsuario'] = $usuarioData['nombres'];
                $_SESSION['rolUsuario'] = $usuarioData['idRol'];

                return ["success" => true, "message" => "✅ Inicio de sesión exitoso."];

            } else {
                return ["success" => false, "message" => "❌ Credenciales incorrectas."];
            }
        } catch (Exception $e) {
             error_log("Excepción en UsuarioController::loginUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado durante el inicio de sesión."];
        }
    }

    public function actualizarUsuario(int $idUsuario, array $data): array {
        // Aquí va la lógica de autorización (verificar permisos)

        $datosValidados = [];
        $erroresValidacion = [];

        if (isset($data['nombres'])) {
            if (!empty(trim($data['nombres']))) { $datosValidados['nombres'] = trim($data['nombres']); }
            else { $erroresValidacion[] = "El nombre no puede estar vacío."; }
        }
        if (isset($data['apellidos'])) {
             if (!empty(trim($data['apellidos']))) { $datosValidados['apellidos'] = trim($data['apellidos']); }
             else { $erroresValidacion[] = "El apellido no puede estar vacío."; }
        }
         if (isset($data['edad'])) {
            if (is_numeric($data['edad']) && $data['edad'] > 0 && $data['edad'] < 120) { $datosValidados['edad'] = (int)$data['edad']; }
            else { $erroresValidacion[] = "Edad inválida."; }
        }
        if (isset($data['correo'])) {
            if (filter_var($data['correo'], FILTER_VALIDATE_EMAIL)) {
                // Podrías añadir verificación de correo duplicado aquí si es necesario
                $datosValidados['correo'] = $data['correo'];
            } else {
                 $erroresValidacion[] = "Formato de correo inválido.";
            }
        }
         if (isset($data['documento'])) {
             if (!empty(trim($data['documento']))) {
                 // Verificar si el nuevo documento ya existe para OTRO usuario
                 $existente = $this->usuarioModel->obtenerUsuarioPorDocumento(trim($data['documento']));
                 if ($existente && $existente['idUsuario'] != $idUsuario) {
                     $erroresValidacion[] = "El documento ya está registrado para otro usuario.";
                 } else {
                      $datosValidados['documento'] = trim($data['documento']);
                 }
             }
             else { $erroresValidacion[] = "El documento no puede estar vacío."; }
        }

        if (!empty($erroresValidacion)) {
            return ["success" => false, "message" => implode(' ', $erroresValidacion)];
        }

        if (empty($datosValidados)) {
            return ["success" => false, "message" => "No se proporcionaron datos válidos para actualizar."];
        }

        try {
            $actualizado = $this->usuarioModel->update($idUsuario, $datosValidados);

            if ($actualizado) {
                // Podrías actualizar la sesión si el usuario se actualiza a sí mismo
                return ["success" => true, "message" => "✅ Usuario actualizado correctamente."];
            } else {
                 if ($this->usuarioModel->obtenerUsuarioPorId($idUsuario)) {
                      return ["success" => false, "message" => "ℹ️ No se realizaron cambios (datos idénticos)."];
                 } else {
                      return ["success" => false, "message" => "❌ No se encontró el usuario para actualizar."];
                 }
            }
        } catch (Exception $e) {
             error_log("Excepción en UsuarioController::actualizarUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado al actualizar."];
        }
    }

    public function eliminarUsuario(int $idUsuario): array {

        try {
            $eliminado = $this->usuarioModel->delete($idUsuario);

            if ($eliminado) {
                 return ["success" => true, "message" => "✅ Usuario eliminado correctamente."];
            } else {
                 return ["success" => false, "message" => "❌ No se pudo eliminar el usuario (puede que no exista o tenga datos asociados)."];
            }
        } catch (Exception $e) {
             error_log("Excepción en UsuarioController::eliminarUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado al eliminar."];
        }
    }
}
?>