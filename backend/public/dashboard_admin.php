<?php
// backend/public/dashboard_admin.php

// Asegurarse de iniciar sesión ANTES de cualquier salida
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// --- CONTROL DE ACCESO (como antes) ---
if (!isset($_SESSION['idUsuario'])) {
    header('Location: login.php');
    exit;
}
if (!isset($_SESSION['rolUsuario']) || $_SESSION['rolUsuario'] != 1) { // 1 = Admin
    header('Location: registroGlucosa.php'); // O index.php
    exit;
}
$nombreAdmin = $_SESSION['nombreUsuario'] ?? 'Administrador';

// --- Cargar Datos para Estadísticas ---
require_once '../config/database.php';
require_once '../models/Usuario.php';
require_once '../models/glucosaModel.php'; // Asegúrate que la ruta y nombre sean correctos
require_once '../models/citasModel.php';    // Incluir el nuevo modelo de citas

$totalUsuarios = 0;
$totalRegistrosGlucosa = 0;
$totalCitas = 0;

try {
    $db = new Conexion();
    $conn = $db->conectar();

    // Instanciar modelos
    $usuarioModel = new UsuarioModel($conn);
    $glucosaModel = new GlucosaModel($conn); // Asume que GlucosaModel usa $conn igual que UsuarioModel
    $citaModel = new CitaModel($conn);

    // Obtener conteos
    $totalUsuarios = $usuarioModel->contarTotalUsuarios();
    $totalRegistrosGlucosa = $glucosaModel->contarTotalRegistros();
    $totalCitas = $citaModel->contarTotalCitas();

} catch (Exception $e) {
    // Manejar error si falla la carga de datos
    error_log("Error cargando datos para dashboard admin: " . $e->getMessage());
    // Puedes definir un mensaje de error para mostrar si quieres
    $errorStats = "No se pudieron cargar las estadísticas.";
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Administrador - Control Glucosa</title>
    <link rel="stylesheet" href="css/styles.css">
    <link rel="stylesheet" href="Css/navbar.css"> <style>
        /* ... (los estilos que tenías antes para .dashboard-container, etc.) ... */
        .dashboard-container { padding: 20px 30px; }
        .welcome-admin { margin-bottom: 25px; padding-bottom: 15px; border-bottom: 1px solid #eee; }
        .dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 25px; margin-top: 20px; }
        .dashboard-card { background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); text-align: center; }
        .dashboard-card h3 { margin-top: 0; margin-bottom: 15px; color: #3058a6; }
        .dashboard-card .stat-number { font-size: 2.5em; font-weight: bold; color: #f45501; display: block; margin-bottom: 10px; }
        .dashboard-card p { margin-bottom: 5px; color: #555; }
        .dashboard-card .card-link { display: inline-block; margin-top: 15px; padding: 8px 15px; background-color: #3058a6; color: white !important; text-decoration: none; border-radius: 4px; transition: background-color 0.3s ease; }
        .dashboard-card .card-link:hover { background-color: #24437c; }
        .error-stats { color: red; font-style: italic; } /* Estilo para mensaje de error */
    </style>
</head>
<body>

    <?php include __DIR__ . '/../includes/navbar_admin.php'; // Incluir Navbar de Admin ?>

    <main class="dashboard-container">
        <h1 class="welcome-admin">Panel de Administración</h1>
        <p>Bienvenido de nuevo, <strong><?php echo htmlspecialchars($nombreAdmin); ?></strong>.</p>

         <?php if (isset($errorStats)): ?>
             <p class="error-stats"><?php echo htmlspecialchars($errorStats); ?></p>
         <?php endif; ?>

        <div class="dashboard-grid">

            <div class="dashboard-card">
                <h3>Estadísticas Rápidas</h3>
                 <p>Usuarios Registrados:</p>
                 <span class="stat-number"><?php echo intval($totalUsuarios); ?></span>
                 <p>Registros Glucosa:</p>
                 <span class="stat-number"><?php echo intval($totalRegistrosGlucosa); ?></span>
                 <p>Citas Programadas:</p>
                 <span class="stat-number"><?php echo intval($totalCitas); ?></span>
            </div>

            <div class="dashboard-card">
                <h3>Gestionar Usuarios</h3>
                <p>Ver, editar o eliminar cuentas de usuario.</p>
                <a href="admin_gestionar_usuarios.php" class="card-link">Ir a Gestión de Usuarios</a>
            </div>

            <div class="dashboard-card">
                 <h3>Ver Registros Globales</h3>
                 <p>Consultar todos los registros del sistema.</p>
                 <a href="admin_ver_registros.php?tipo=glucosa" class="card-link">Ver Registros Glucosa</a>
                 <br>
                  <a href="admin_ver_registros.php?tipo=citas" class="card-link" style="margin-top: 10px;">Ver Registros Citas</a>
            </div>

            <div class="dashboard-card">
                 <h3>Configuración</h3>
                 <p>Ajustes generales del sistema.</p>
                 <a href="#" class="card-link">Ir a Configuración</a>
            </div>

        </div>
    </main>

    </body>
</html>