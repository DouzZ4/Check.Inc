<?php
session_start(); 
if (!isset($_SESSION['idUsuario'])) {
    $_SESSION['message'] = "Por favor, inicia sesión para acceder a la página.";
    header('Location: login.php'); // Redirige al formulario de inicio de sesión
    exit;
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro Citas</title>
</head>
<body>
    
    <header>
        <h1>Registro de Citas</h1>
    </header>
    <main>
        <form id="citaForm" method="POST" action="../controllers/citaController.php">
            <input type="date" name="fecha" placeholder="Fecha" required>
            <input type="time" name="hora" placeholder="Hora" required>
            <input type="text" name="motivo" placeholder="Motivo" required>
            <button type="submit">Registrar Cita</button>
        </form>

        <?php if (isset($_SESSION['message'])): ?>
            <div class="mensaje"><?php echo htmlspecialchars($_SESSION['message']); ?></div>
            <?php unset($_SESSION['message']); ?>
        <?php endif; ?>

        <h2>Mis Citas</h2>
        <table id="citasTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Motivo</th>
                </tr>
            </thead>
            <tbody></tbody>
        </table>

    </main>
</body>
</html>