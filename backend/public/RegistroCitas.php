<?php
session_start();
if (!isset($_SESSION['idUsuario'])) {
    $_SESSION['message'] = "Por favor, inicia sesión para acceder a la página.";
    header('Location: login.php'); // Redirige al formulario de inicio de sesión
    exit;
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Citas</title>
    <style>
        /* Estilos... */
    </style>
</head>
<body>
    <header>
        <h1>Registro de Citas</h1>
    </header>
    <main>
        <p id="message">
            <?php
            if (isset($_SESSION['message'])) {
                echo $_SESSION['message'];
                unset($_SESSION['message']);
            }
            ?>
        </p>

        <h2>Registrar Cita</h2>
        <form id="citaForm">
            <input type="date" id="fecha" placeholder="Fecha" required>
            <input type="time" id="hora" placeholder="Hora" required>
            <input type="text" id="motivo" placeholder="Motivo" required>
            <button type="button" onclick="registrarCita()">Registrar Cita</button>
        </form>

        <h2>Mis Citas</h2>
        <table>
            <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Motivo</th>
                </tr>
            </thead>
            <tbody id="citasTable"></tbody>
        </table>
    </main>
    <script>
        async function registrarCita() {
            const fecha = document.getElementById('fecha').value;
            const hora = document.getElementById('hora').value;
            const motivo = document.getElementById('motivo').value;
            const idUsuario = <?php echo $_SESSION['idUsuario']; ?>;

            const response = await fetch('../routes/citasRoutes.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fecha, hora, motivo, idUsuario })
            });

            const result = await response.json();
            // Mensajes y actualización de tabla...
        }
    </script>
</body>
</html>