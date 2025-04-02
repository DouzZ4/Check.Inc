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
    <title>Gestión de Citas</title>
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
        h1, h2 {
            color: #4CAF50;
        }
        form input, form button {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            border-radius: 5px;
            border: 1px solid #ddd;
        }
        form button {
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        form button:hover {
            background-color: #45A049;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        table th, table td {
            border: 1px solid #ddd;
            text-align: center;
            padding: 10px;
        }
        table th {
            background-color: #4CAF50;
            color: white;
        }
        .message {
            font-weight: bold;
            margin-bottom: 20px;
            color: red;
        }
    </style>
</head>
<body>
    <nav>
        <div>
            <a href="index.php">Inicio</a>
            <a href="RegistroCitas.php">Gestión de Citas</a>
        </div>
        <div>
            <span>Bienvenido, <?php echo htmlspecialchars($_SESSION['user']); ?>!</span>
            <a href="logout.php" style="margin-left: 15px;">Cerrar Sesión</a>
        </div>
    </nav>
    <div class="container">
        <h1>Gestión de Citas</h1>
        <!-- Mostrar mensajes -->
        <p id="message" class="message">
            <?php
            if (isset($_SESSION['message'])) {
                echo $_SESSION['message'];
                unset($_SESSION['message']);
            }
            ?>
        </p>

        <!-- Formulario para registrar citas -->
        <h2>Registrar Nueva Cita</h2>
        <form id="citaForm">
            <input type="date" id="fecha" required placeholder="Fecha">
            <input type="time" id="hora" required placeholder="Hora">
            <input type="text" id="motivo" required placeholder="Motivo">
            <button type="button" onclick="registrarCita()">Registrar</button>
        </form>

        <!-- Tabla para mostrar citas -->
        <h2>Historial de Citas</h2>
        <table>
            <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Motivo</th>
                </tr>
            </thead>
            <tbody id="citasTable">
                <!-- Las filas se generan dinámicamente con JavaScript -->
            </tbody>
        </table>
    </div>

    <script>
        // Función para registrar una cita
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
            const messageElement = document.getElementById('message');

            if (result.status === 'success') {
                messageElement.textContent = result.message;
                messageElement.style.color = "green";
                obtenerCitas(); // Refrescar la tabla de citas
            } else {
                messageElement.textContent = result.message;
                messageElement.style.color = "red";
            }
        }

        // Función para obtener citas del historial
        async function obtenerCitas() {
            const idUsuario = <?php echo $_SESSION['idUsuario']; ?>;

            const response = await fetch(`../routes/citasRoutes.php?idUsuario=${idUsuario}`);
            const result = await response.json();

            const citasTable = document.getElementById('citasTable');
            citasTable.innerHTML = ''; // Limpiar la tabla

            if (result.status === 'success') {
                const citas = result.data;
                citas.forEach(cita => {
                    const row = `
                        <tr>
                            <td>${cita.fecha}</td>
                            <td>${cita.hora}</td>
                            <td>${cita.motivo}</td>
                        </tr>
                    `;
                    citasTable.innerHTML += row;
                });
            } else {
                const messageElement = document.getElementById('message');
                messageElement.textContent = result.message;
                messageElement.style.color = "red";
            }
        }

        // Obtener citas al cargar la página
        window.onload = obtenerCitas;
    </script>
</body>
</html>