<?php
// includes/navbar.php
// ... (definición de $baseUrl como antes) ...
$baseUrl = '/check.inc'; // Asegúrate que esta variable esté definida
?>
<nav class="navbar">
    <div class="navbar-container">
        <div class="navbar-branding">
            <a href="<?php echo $baseUrl; ?>/index.php" style="text-decoration: none; display: flex; align-items: center;">
                <div id="logo-placeholder">Logo</div>
                <h1>Control Glucosa</h1>
            </a>
        </div>
        <div class="navbar-links">
            <a href="<?php echo $baseUrl; ?>/index.php">Inicio</a>
            <?php if (isset($_SESSION['idUsuario'])): ?>
                <a href="<?php echo $baseUrl; ?>/backend/public/registroGlucosa.php">Registro Glucosa</a>
                <a href="<?php echo $baseUrl; ?>/backend/public/registroCitas.php">Citas</a>
                <a href="#">Anomalía</a>
            <?php endif; ?>
        </div>
        <div class="navbar-user-info">
            <?php if (isset($_SESSION['idUsuario'])): ?>
                <span class="welcome-message">Hola, <?php echo isset($_SESSION['nombreUsuario']) ? htmlspecialchars($_SESSION['nombreUsuario']) : 'Usuario'; ?>!</span>

                <a href="<?php echo $baseUrl; ?>/backend/public/logout.php" class="button button-logout">Cerrar Sesión</a>
                <?php else: ?>
                <a href="<?php echo $baseUrl; ?>/backend/public/login.php" class="button button-login">Iniciar Sesión</a>
                <a href="<?php echo $baseUrl; ?>/backend/public/registrousuario.php" class="button button-register">Registrarse</a>
            <?php endif; ?>
        </div>
    </div>
</nav>