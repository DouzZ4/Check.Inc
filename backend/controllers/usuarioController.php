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
        // 1️⃣ Validar si todos los campos obligatorios están presentes
        if (!isset($data['user'], $data['password'], $data['documento'], $data['nombres'], 
                   $data['apellidos'], $data['correo'], $data['edad'], $data['idRol'])) {
            echo json_encode(["message" => "❌ Faltan datos obligatorios."]);
            return;
        }
    
        // 2️⃣ Validar cada campo
        // Usuario
        if (strlen($data['user']) < 5 || strlen($data['user']) > 15) {
            echo json_encode(["message" => "❌ El usuario debe tener entre 5 y 15 caracteres."]);
            return;
        }
    
        // Contraseña
        if (strlen($data['password']) < 8) {
            echo json_encode(["message" => "❌ La contraseña debe tener al menos 8 caracteres."]);
            return;
        }
    
        // Documento
        if (!is_numeric($data['documento']) || $data['documento'] <= 0) {
            echo json_encode(["message" => "❌ El documento debe ser un número positivo."]);
            return;
        }
    
        // Nombres y apellidos
        if (empty(trim($data['nombres'])) || empty(trim($data['apellidos']))) {
            echo json_encode(["message" => "❌ Los nombres y apellidos no pueden estar vacíos."]);
            return;
        }
    
        // Correo electrónico
        if (!filter_var($data['correo'], FILTER_VALIDATE_EMAIL)) {
            echo json_encode(["message" => "❌ El correo electrónico no tiene un formato válido."]);
            return;
        }
    
        // Edad
        if (!is_numeric($data['edad']) || $data['edad'] < 1 || $data['edad'] > 120) {
            echo json_encode(["message" => "❌ La edad debe ser un número entre 1 y 120."]);
            return;
        }
    
        // Rol del usuario
        if ($data['idRol'] !== "1" && $data['idRol'] !== "2") {
            echo json_encode(["message" => "❌ El rol del usuario es inválido."]);
            return;
        }
    
        // 3️⃣ Si las validaciones son exitosas, procede a registrar el usuario
        $this->usuario->setUser($data['user']);
        $this->usuario->setPassword($data['password']);
        $this->usuario->setDocumento($data['documento']);
        $this->usuario->setNombres($data['nombres']);
        $this->usuario->setApellidos($data['apellidos']);
        $this->usuario->setCorreo($data['correo']);
        $this->usuario->setEdad($data['edad']);
        $this->usuario->setIdRol($data['idRol']);
    
        $resultado = $this->usuario->registrar();
        echo json_encode($resultado ? ["message" => "✅ Usuario registrado correctamente."] : ["message" => "❌ Error al registrar usuario."]);
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