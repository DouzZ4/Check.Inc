<?php

require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../models/Usuario.php';

// Conectamos a la base de datos
$db = Conexion::conectar();

// Obtenemos los roles
$query = "SELECT * FROM rol";
$stmt = $db->prepare($query);
$stmt->execute();
$roles = $stmt->fetchAll(PDO::FETCH_ASSOC);

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $user = $_POST['user'];
    $password = $_POST['password'];
    $documento = $_POST['documento'];
    $nombres = $_POST['nombres'];
    $apellidos = $_POST['apellidos'];
    $correo = $_POST['correo'];
    $edad = $_POST['edad'];
    $idRol = $_POST['idRol'];

    $usuario = new Usuario();
    if ($usuario->registrarUsuario($user, $password, $documento, $nombres, $apellidos, $correo, $edad, $idRol)) {
        echo "<p>Usuario registrado exitosamente.</p>";
    } else {
        echo "<p>Error al registrar usuario.</p>";
    }
}

?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Usuario</title>
    <link rel="stylesheet" href="../assets/styles.css">
</head>
<body>
    <h2>Registro de Usuario</h2>
    <form method="POST" action="">
        <label for="user">Usuario:</label>
        <input type="text" id="user" name="user" required>

        <label for="password">Contraseña:</label>
        <input type="password" id="password" name="password" required>

        <label for="documento">Documento:</label>
        <input type="number" id="documento" name="documento" required>

        <label for="nombres">Nombres:</label>
        <input type="text" id="nombres" name="nombres" required>

        <label for="apellidos">Apellidos:</label>
        <input type="text" id="apellidos" name="apellidos" required>

        <label for="correo">Correo Electrónico:</label>
        <input type="email" id="correo" name="correo" required>

        <label for="edad">Edad:</label>
        <input type="number" id="edad" name="edad" required>

        <label for="idRol">Rol:</label>
        <select id="idRol" name="idRol" required>
            <?php foreach ($roles as $rol): ?>
                <option value="<?= $rol['idRol'] ?>"><?= $rol['nombre'] ?></option>
            <?php endforeach; ?>
        </select>

        <button type="submit">Registrar</button>
    </form>
</body>
</html>