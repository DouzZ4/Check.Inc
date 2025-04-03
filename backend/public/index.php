<?php
session_start(); // Iniciar sesión al principio de todo
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inicio - Sistema de Glucosa</title>
    <link rel="stylesheet" href="./css/styles.css">
    <link rel="stylesheet" href="./css/navbar.css">
    <link rel="stylesheet" href="./css/index.css">
</head>
<body>
    <nav class="navbar">
        <div class="navbar-container">
            <div class="navbar-branding">
                <div id="logo-placeholder">Logo Aquí</div>
                <h1>Control Glucosa</h1>
            </div>
            <div class="navbar-links"> <a href="index.php">Inicio</a>
                 <?php if (isset($_SESSION['idUsuario'])): ?>
                     <a href="./registroGlucosa.php">Registro Glucosa</a>
                     <a href="./RegistroCitas.php">Citas</a> <a href="./public/RegistroAnomalia.php">Anomalia</a> <?php endif; ?>
            </div>
            <div class="navbar-user-info">
                <?php if (isset($_SESSION['idUsuario'])): ?>
                    <span>Bienvenido, <?php echo isset($_SESSION['nombreUsuario']) ? htmlspecialchars($_SESSION['nombreUsuario']) : 'Usuario'; ?>!</span>
                    <a href="../routes/logout.php" class="button button-secondary" style="margin-left: 15px;">Cerrar Sesión</a>
                <?php else: ?>
                    <a href="./login.php" class="button">Iniciar Sesión</a>
                <?php endif; ?>
            </div>
        </div>
    </nav>
    <div class="container">
        <h1>Sistema de Gestión de Glucosa</h1>
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