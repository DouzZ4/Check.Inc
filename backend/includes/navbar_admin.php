<?php
// includes/navbar_admin.php
// Asumimos session_start() ya fue llamado por el script que incluye este archivo.

// Definir $baseUrl (Ajusta '/check.inc/' si es necesario)
$baseUrl = '/check.inc';

// Obtener nombre del admin de la sesión para bienvenida
// Usamos la misma variable de sesión que usa el navbar normal
$nombreAdminNav = $_SESSION['nombreUsuario'] ?? 'Admin';
?>
<nav class="navbar navbar-admin"> <div class="navbar-container">

        <div class="navbar-branding">
            <a href="<?php echo $baseUrl; ?>/backend/public/dashboard_admin.php" style="text-decoration: none; display: flex; align-items: center;">
                <img src="<?php echo $baseUrl; ?>/backend/public/images/ICONO.png" alt="Logo Check" id="logo-navbar">
                <h1>Panel Admin</h1>
            </a>
        </div>

        <div class="navbar-links">
            <a href="<?php echo $baseUrl; ?>/backend/public/dashboard_admin.php">Dashboard</a>
            <a href="<?php echo $baseUrl; ?>/backend/public/admin_gestionar_usuarios.php">Gestionar Usuarios</a>
            <a href="<?php echo $baseUrl; ?>/backend/public/admin_ver_registros.php?tipo=glucosa">Ver Registros Glucosa</a>
            <a href="<?php echo $baseUrl; ?>/backend/public/admin_ver_registros.php?tipo=citas">Ver Registros Citas</a>
             </div>

        <div class="navbar-user-info">
            <span class="welcome-message">Admin: <?php echo htmlspecialchars($nombreAdminNav); ?></span>
            <a href="<?php echo $baseUrl; ?>/backend/public/logout.php" class="button button-logout">Cerrar Sesión</a>
             </div>

    </div>
</nav>