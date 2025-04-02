<?php
session_start();
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inicio - Sistema de Glucosa</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
        }
        nav {
            background-color: #4CAF50;
            color: white;
            padding: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        nav a {
            color: white;
            text-decoration: none;
            margin: 0 15px;
            font-weight: bold;
        }
        nav a:hover {
            text-decoration: underline;
        }
        .container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            margin: 10px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .button:hover {
            background-color: #45A049;
        }
    </style>
</head>
<body>
    <nav>
        <div>
            <a href="index.php">Inicio</a>
            <?php if (isset($_SESSION['idUsuario'])): ?>
                <a href="registroGlucosa.php">Registro de Glucosa</a>
            <?php endif; ?>
        </div>
        <div>
            <?php if (isset($_SESSION['idUsuario'])): ?>
                <span>Bienvenido, <?php echo htmlspecialchars($_SESSION['user']); ?>!</span>
                <a href="logout.php" class="button">Cerrar Sesión</a>
            <?php else: ?>
                <a href="login.php" class="button">Iniciar Sesión</a>
            <?php endif; ?>
        </div>
    </nav>
    <div class="container">
        <h1>Sistema de Gestión de Glucosa</h1>
        <p>
            Este sistema te ayuda a registrar y monitorear tus niveles de glucosa en sangre.
            Inicia sesión o regístrate para comenzar.
        </p>
        <?php if (!isset($_SESSION['idUsuario'])): ?>
            <a href="login.php" class="button">Iniciar Sesión</a>
            <a href="registroUsuario.php" class="button">Registrarse</a>
        <?php else: ?>
            <a href="registroGlucosa.php" class="button">Ir al Registro de Glucosa</a>
        <?php endif; ?>
    </div>
</body>
</html>