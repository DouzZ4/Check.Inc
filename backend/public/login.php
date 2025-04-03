<?php
// Iniciar sesión ANTES de cualquier salida HTML
session_start();
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inicio de Sesión - Control Glucosa</title>
    <link rel="stylesheet" href="./css/login.css">
    <link rel="stylesheet" href="./css/navbar.css"> </head>
<body>
    <nav class="navbar">
         <div class="navbar-container">
             <div class="navbar-branding">
                 <div id="logo-placeholder">Logo Aquí</div>
                 <h1>Control Glucosa</h1>
             </div>
             <div class="navbar-user-info">
                 <a href="registrousuario.php" style="color: white;">Registrarse</a>
             </div>
         </div>
     </nav>

    <main class="form-container login-container"> <h2>Iniciar Sesión</h2>

        <?php
        // Mostrar mensaje flash de sesión (para errores de login o éxito de registro)
        if (isset($_SESSION['message'])) {
            $message_type = $_SESSION['message_type'] ?? 'info';
            $message_class = 'message-' . htmlspecialchars($message_type);
            $msg = htmlspecialchars($_SESSION['message']);
            // Usar una clase contenedora para el mensaje
            echo "<div class='message-box {$message_class}'>{$msg}</div>";
            unset($_SESSION['message']);
            unset($_SESSION['message_type']);
        }
        ?>

        <form id="loginForm" action="../routes/usuarioRoutes.php?login" method="POST">
            <div class="form-group">
                <label for="user">Nombre de Usuario:</label>
                <input type="text" id="user" name="user" required autofocus> </div>

            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="submit-button">Iniciar Sesión</button>

            <div class="register-link">
                 ¿No tienes cuenta? <a href="registrousuario.php">Regístrate aquí</a>
            </div>
        </form>
    </main>

     </body>
</html>