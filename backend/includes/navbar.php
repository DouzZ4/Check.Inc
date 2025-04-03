<?php
// includes/navbar.php
// NOTA: Asumimos que session_start() ya fue llamado por el script principal que incluye este archivo.
// Si no estás seguro, puedes añadir: if (session_status() === PHP_SESSION_NONE) { session_start(); }
// pero es mejor manejarlo en el script principal.

// Define la ruta base de tu aplicación para construir URLs absolutas
// Ajusta '/check.inc/' si tu proyecto está en una subcarpeta diferente o en la raíz ('/')
$baseUrl = '/check.inc';
?>
<nav class="navbar">
    <div class="navbar-container">
        <div class="navbar-branding">
            <a href="<?php echo $baseUrl; ?>/index.php" style="text-decoration: none; display: flex; align-items: center;">
                <div id="logo-placeholder">Logo</div>
                <h1>CHECK</h1>
            </a>
            </div>
        <div class="navbar-links">
            <a href="<?php echo $baseUrl; ?>/index.php">Inicio</a>
            <?php if (isset($_SESSION['idUsuario'])): ?>
                <a href="<?php echo $baseUrl; ?>/backend/public/registroGlucosa.php">Registro Glucosa</a>
                <a href="#">Citas</a> <a href="#">Anomalía</a> <?php /* if ($_SESSION['rolUsuario'] == 1): ?>
                    <a href="<?php echo $baseUrl; ?>/backend/public/admin/usuarios.php">Gestionar Usuarios</a>
                <?php endif; */ ?>
            <?php endif; ?>
        </div>
        <div class="navbar-user-info">
            <?php if (isset($_SESSION['idUsuario'])): ?>
                <span class="welcome-message">Hola, <?php echo isset($_SESSION['nombreUsuario']) ? htmlspecialchars($_SESSION['nombreUsuario']) : 'Usuario'; ?>!</span>
                <a href="<?php echo $baseUrl; ?>/backend/routes/logout.php" class="button button-logout">Cerrar Sesión</a>
            <?php else: ?>
                <a href="<?php echo $baseUrl; ?>/backend/public/login.php" class="button button-login">Iniciar Sesión</a>
                <a href="<?php echo $baseUrl; ?>/backend/public/registrousuario.php" class="button button-register">Registrarse</a>
            <?php endif; ?>
        </div>
    </div>
</nav>