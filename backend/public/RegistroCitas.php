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
<<<<<<< HEAD
        <form id="citaForm">
            <input type="date" id="fecha" placeholder="Fecha" required>
            <input type="time" id="hora" placeholder="Hora" required>
            <input type="text" id="motivo" placeholder="Motivo" required>
=======
        <form id="citaForm" aria-labelledby="form-title">
            <label for="fecha">Fecha</label>
            <input type="date" name="fecha" placeholder="Fecha" required>

            <label for="hora">Hora</label>
            <input type="time" name="hora" placeholder="hora" required>

            <label for="motivo">Motivo</label>
            <input type="text" name="motivo" placeholder="Motivo" required>

>>>>>>> 3189f05562512c1e767744ac870eb5aa108f7e61
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
<<<<<<< HEAD
            <tbody id="citasTable"></tbody>
=======
            <tbody id="citasBody">
                <!-- Aquí se llenan las citas con JavaScript -->
            </tbody>
>>>>>>> 3189f05562512c1e767744ac870eb5aa108f7e61
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
<<<<<<< HEAD
            // Mensajes y actualización de tabla...
=======
            messageElement.textContent = result.message;
            messageElement.style.color = result.status ==='success'? "green" : "red";

            if (result.status ==='success') {
                obtenerCitas(); // Refrescar la tabla de citas
            }
        }

        // Función para obtener citas registradas
        async function obtenerCitas() {
            const idUsuario = <?php echo $_SESSION['idUsuario']; ?>;

            try {
                const response = await fetch(`../routes/citasRoutes.php?idUsuario=${idUsuario}`);
                const citas = await response.json();

                const citasBody = document.getElementById('citasBody');
                citasBody.innerHTML = ''; // Limpia la tabla

                if (citas.status === 'success') {
                    const registros = citas.data; // Usa la respuesta JSON correctamente
                    registros.forEach(registro => {
                        const row = `
                            <tr>
                                <td>${registro.fecha}</td>
                                <td>${registro.hora}</td>
                                <td>${registro.motivo}</td>
                            </tr>
                        `;
                        citasBody.innerHTML += row; // Usa el ID correcto de la tabla
                    });
                } else {
                    alert(citas.message); // Muestra el mensaje de error si no es exitoso
                }
            } catch (error) {
                console.error('Error al obtener las citas:', error);
                alert('Hubo un error al obtener las citas.');
            }
>>>>>>> 3189f05562512c1e767744ac870eb5aa108f7e61
        }
    </script>
</body>
</html>