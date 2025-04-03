<?php
session_start(); // Iniciar sesión al principio de todo
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inicio - CHECK</title>
    <link rel="stylesheet" href="./Css/styles.css">
    <link rel="stylesheet" href="./Css/navbar.css">
    <link rel="stylesheet" href="./Css/index.css">
</head>
<body>
    <?php include __DIR__ . '/../includes/navbar.php'; ?>
    <div class="container">
        <h1>CHECK</h1>
        <p>
            Bienvenido a la plataforma para registrar y monitorear tus niveles de glucosa en sangre.
            <?php if (!isset($_SESSION['idUsuario'])): ?>
                 Por favor, inicia sesión o regístrate para acceder a todas las funcionalidades.
            <?php else: ?>
                 Navega usando el menú superior o accede directamente a tus registros.
            <?php endif; ?>
        </p>

        <?php if (!isset($_SESSION['idUsuario'])): ?>
            <a href="./login.php" class="button">Iniciar Sesión</a>
            <a href="./registrousuario.php" class="button button-secondary">Registrarse</a>
        <?php else: ?>
            <a href="./registroGlucosa.php" class="button">Ir al Registro de Glucosa</a>
            <?php endif; ?>
    </div>
    </body>
</html>