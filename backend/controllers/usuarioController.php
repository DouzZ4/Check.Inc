<?php
// controllers/UsuarioController.php

// Requerir el archivo con Entidad, Factory y ¡AHORA el Modelo!
require_once '../models/Usuario.php';

class UsuarioController {
    // Ahora depende del Modelo, no de la conexión directa
    private $usuarioModel;

    // Constructor que recibe la instancia del Modelo
    public function __construct(UsuarioModel $usuarioModel) { // Inyectar el Modelo
        $this->usuarioModel = $usuarioModel;
    }

    // Método para registrar un usuario (paciente)
    public function registrarUsuario($data): array { // Añadir tipo de retorno array
        try {
             // 1. Forzar Rol y Validar Datos de Entrada Básicos (si aplica)
             $data['idRol'] = 2; // Rol paciente
             if (empty($data['nombres']) || empty($data['apellidos']) || empty($data['edad']) || empty($data['correo']) || empty($data['user']) || empty($data['documento']) || empty($data['password'])) {
                 return ["success" => false, "message" => "❌ Todos los campos son requeridos."];
             }

             // 2. Crear Entidad con Factory (pasa contraseña SIN hashear)
             $usuario = UsuarioFactory::crearUsuario(
                 $data['nombres'],
                 $data['apellidos'],
                 $data['edad'],
                 $data['correo'],
                 $data['user'],
                 $data['documento'],
                 $data['password'], // Contraseña en texto plano
                 $data['idRol']
             );

             // 3. Validar usando métodos de la Entidad
             $validacionUsuario = $usuario->validarUsuario();
             if ($validacionUsuario !== true) {
                 return ["success" => false, "message" => $validacionUsuario];
             }
             $validacionPassword = $usuario->validarPassword();
             if ($validacionPassword !== true) {
                 $mensajeErrors = is_array($validacionPassword) ? implode(' ', $validacionPassword) : $validacionPassword;
                 return ["success" => false, "message" => $mensajeErrors];
             }

             // 4. Comprobar si usuario ya existe ANTES de intentar registrar (¡NUEVO!)
             // Llama al método del modelo que busca por nombre de usuario
             if ($this->usuarioModel->obtenerUsuarioPorUser($usuario->getUser()) !== null) {
                 return ["success" => false, "message" => "❌ El nombre de usuario ya está en uso."];
             }
             // Podrías añadir una comprobación similar para el correo si debe ser único

             // 5. Llamar al Modelo para registrar (el Modelo hashea la contraseña)
             $registrado = $this->usuarioModel->registrarUsuario($usuario);

             // 6. Devolver respuesta basada en el resultado del Modelo
             if ($registrado) {
                 return ["success" => true, "message" => "✅ Usuario registrado correctamente."];
             } else {
                 // El modelo ya logueó el error PDO, aquí damos mensaje genérico
                 return ["success" => false, "message" => "❌ Error del servidor al registrar usuario. Inténtalo más tarde."];
             }
         } catch (Exception $e) {
            // Captura excepciones inesperadas (ej. de la Factory si la modificas)
             error_log("Excepción en UsuarioController::registrarUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado."];
         }
    }

    // Método para el inicio de sesión
    public function loginUsuario($data): array { // Añadir tipo de retorno array
        if (empty($data['user']) || empty($data['password'])) {
            return ["success" => false, "message" => "❌ Usuario y contraseña son requeridos."];
        }

        try {
            // 1. Obtener usuario usando el Modelo
            $usuarioData = $this->usuarioModel->obtenerUsuarioPorUser($data['user']);

            // 2. Verificar si el usuario existe y la contraseña es correcta
            if ($usuarioData && isset($usuarioData['password']) && password_verify($data['password'], $usuarioData['password'])) {

                // ¡ÉXITO! Iniciar sesión (Crear variables de sesión)
                session_start(); // Asegúrate que la sesión esté iniciada
                $_SESSION['idUsuario'] = $usuarioData['idUsuario']; // Asumiendo que la columna es idUsuario
                $_SESSION['nombreUsuario'] = $usuarioData['nombres']; // Guardar nombre para mostrar
                $_SESSION['rolUsuario'] = $usuarioData['idRol']; // Guardar rol para permisos

                return ["success" => true, "message" => "✅ Inicio de sesión exitoso."];

            } else {
                // Usuario no encontrado o contraseña incorrecta
                return ["success" => false, "message" => "❌ Credenciales incorrectas."];
            }
        } catch (Exception $e) {
             error_log("Excepción en UsuarioController::loginUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado durante el inicio de sesión."];
        }
    }

  // --- NUEVO: Método para Actualizar Usuario ---
    /**
     * Maneja la actualización de datos de un usuario.
     * @param int $idUsuario ID del usuario a actualizar (usualmente de la URL).
     * @param array $data Datos recibidos (usualmente del body PUT/POST).
     * @return array Respuesta estándar ['status' => ..., 'message' => ...].
     */
    public function actualizarUsuario(int $idUsuario, array $data): array {
        // --- IMPORTANTE: AUTORIZACIÓN ---
        // Aquí deberías verificar si el usuario logueado ($_SESSION['idUsuario'], $_SESSION['rolUsuario'])
        // tiene permiso para actualizar al usuario con $idUsuario.
        // Ej: Un usuario solo puede actualizar su propio perfil (si $idUsuario == $_SESSION['idUsuario'])
        // O un admin (ej. $_SESSION['rolUsuario'] == 1) puede actualizar a cualquiera.
        // Si no tiene permiso, devuelve un error de autorización (ej. 403 Forbidden).
        // Ejemplo simple (permitir solo actualizarse a sí mismo):
        /*
        session_start(); // Asegurar que la sesión esté activa
        if (!isset($_SESSION['idUsuario']) || $_SESSION['idUsuario'] !== $idUsuario) {
             // Podrías permitir admin aquí con: && (!isset($_SESSION['rolUsuario']) || $_SESSION['rolUsuario'] != 1)
             return ["success" => false, "message" => "❌ No autorizado para actualizar este usuario."];
             // Considera devolver un código HTTP 403 desde la ruta.
        }
        */

        // --- Validación de los Datos Recibidos ---
        // Validar solo los campos permitidos y sus formatos.
        // Excluimos 'user', 'password', 'idRol' de la actualización aquí.
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
                // Opcional: Verificar si el nuevo correo ya está en uso por OTRO usuario
                // $existente = $this->usuarioModel->obtenerUsuarioPorCorreo($data['correo']);
                // if ($existente && $existente['idUsuario'] != $idUsuario) {
                //     $erroresValidacion[] = "El correo electrónico ya está en uso por otro usuario.";
                // } else {
                    $datosValidados['correo'] = $data['correo'];
                // }
            } else {
                 $erroresValidacion[] = "Formato de correo inválido.";
            }
        }
         if (isset($data['documento'])) { // Asumimos que el documento puede cambiar
             if (!empty(trim($data['documento']))) { $datosValidados['documento'] = trim($data['documento']); }
             else { $erroresValidacion[] = "El documento no puede estar vacío."; }
        }

        // Si hubo errores de validación
        if (!empty($erroresValidacion)) {
            return ["success" => false, "message" => implode(' ', $erroresValidacion)];
        }

        // Si no se envió ningún dato válido/permitido para actualizar
        if (empty($datosValidados)) {
            return ["success" => false, "message" => "No se proporcionaron datos válidos para actualizar."];
        }


        try {
            // Llamar al Modelo para actualizar
            $actualizado = $this->usuarioModel->update($idUsuario, $datosValidados);

            // Interpretar resultado
            if ($actualizado) {
                // Opcional: Si el usuario actualizó su propio nombre, actualizar la sesión
                // if ($idUsuario === $_SESSION['idUsuario'] && isset($datosValidados['nombres'])) {
                //    $_SESSION['nombreUsuario'] = $datosValidados['nombres'];
                // }
                return ["success" => true, "message" => "✅ Usuario actualizado correctamente."];
            } else {
                 // El modelo devuelve false si no se afectaron filas (no encontrado o datos iguales) o error PDO
                 // Verificar si existe para dar mejor mensaje
                 if ($this->usuarioModel->obtenerUsuarioPorId($idUsuario)) { // Asumiendo que existe obtenerUsuarioPorId
                      return ["success" => false, "message" => "ℹ️ No se realizaron cambios (datos idénticos)."];
                 } else {
                      return ["success" => false, "message" => "❌ No se encontró el usuario para actualizar."];
                 }
                 // Nota: Necesitarías añadir obtenerUsuarioPorId al modelo si no existe
            }
        } catch (Exception $e) {
             error_log("Excepción en UsuarioController::actualizarUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado al actualizar."];
        }
    }


    // --- NUEVO: Método para Eliminar Usuario ---
    /**
     * Maneja la eliminación de un usuario.
     * @param int $idUsuario ID del usuario a eliminar (usualmente de la URL).
     * @return array Respuesta estándar.
     */
    public function eliminarUsuario(int $idUsuario): array {
        // --- IMPORTANTE: AUTORIZACIÓN ---
        // Aquí deberías verificar si el usuario logueado tiene permiso para ELIMINAR.
        // ¡Generalmente solo Admins! Es peligroso permitir que usuarios se eliminen a sí mismos
        // o a otros sin control.
        // Ejemplo: Solo permitir si es Admin (rol 1)
        /*
        session_start();
        if (!isset($_SESSION['rolUsuario']) || $_SESSION['rolUsuario'] != 1) { // Asumiendo rol 1 = Admin
             return ["success" => false, "message" => "❌ No autorizado para eliminar usuarios."];
             // Considera devolver un código HTTP 403 desde la ruta.
        }
        // También evita que un admin se elimine a sí mismo si es necesario
        if ($_SESSION['idUsuario'] === $idUsuario) {
            return ["success" => false, "message" => "❌ No puedes eliminar tu propia cuenta de administrador."];
        }
        */

        try {
            // Llamar al Modelo para eliminar
            $eliminado = $this->usuarioModel->delete($idUsuario);

            // Interpretar resultado
            if ($eliminado) {
                 return ["success" => true, "message" => "✅ Usuario eliminado correctamente."];
            } else {
                  // El modelo devuelve false si no encontró el ID o si hubo error (ej. restricción FK)
                 return ["success" => false, "message" => "❌ No se pudo eliminar el usuario (puede que no exista o tenga datos asociados)."];
            }
        } catch (Exception $e) {
             error_log("Excepción en UsuarioController::eliminarUsuario: " . $e->getMessage());
             return ["success" => false, "message" => "❌ Ocurrió un error inesperado al eliminar."];
        }
    }
}
?>