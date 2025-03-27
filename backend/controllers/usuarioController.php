<?php
require_once '../models/Usuario.php';

class UsuarioController {
    private $usuario;

    public function __construct() {
        // Inicializamos la instancia de Usuario
        $conexion = new Conexion();
        $this->usuario = new Usuario($conexion->conectar());
    }

    // 🔹 Registrar usuario
    public function registrarUsuario($data) {
        // Verificar que todos los datos obligatorios estén presentes
        if (isset($data['user'], $data['password'], $data['documento'], $data['nombres'], 
                  $data['apellidos'], $data['correo'], $data['edad'], $data['idRol'])) {

            // Usar setters para asignar los valores
            $this->usuario->setUser($data['user']);
            $this->usuario->setPassword($data['password']);
            $this->usuario->setDocumento($data['documento']);
            $this->usuario->setNombres($data['nombres']);
            $this->usuario->setApellidos($data['apellidos']);
            $this->usuario->setCorreo($data['correo']);
            $this->usuario->setEdad($data['edad']);
            $this->usuario->setIdRol($data['idRol']);

            // Registrar el usuario
            $resultado = $this->usuario->registrar();

            echo json_encode($resultado);
        } else {
            // Respuesta en caso de datos incompletos
            echo json_encode(["message" => "❌ Faltan datos obligatorios."]);
        }
    }

    // 🔹 Obtener usuario por ID
    public function obtenerUsuario($id) {
        // Llamamos al método del modelo para obtener el usuario por ID
        $usuarioData = $this->usuario->obtenerPorId($id);

        // Si se encuentra el usuario, devolvemos los datos
        if ($usuarioData) {
            echo json_encode($usuarioData);
        } else {
            echo json_encode(["message" => "❌ Usuario no encontrado."]);
        }
    }

    // 🔹 Login usuario
    public function loginUsuario($data) {
        // Verificar que todos los datos necesarios estén presentes
        if (!isset($data['user']) || !isset($data['password'])) {
            echo json_encode(["message" => "❌ Faltan datos obligatorios."]);
            return;
        }

        // Usar los setters para inicializar los valores
        $this->usuario->setUser($data['user']);
        $this->usuario->setPassword($data['password']); // No se necesita aquí porque la verificación está en el modelo

        // Llamamos al método login del modelo
        $resultado = $this->usuario->login($data['user'], $data['password']);

        echo json_encode($resultado);
    }
}
?>