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
    <title>Citas</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
        }
        header {
            background-color: #4CAF50;
            color: white;
            text-align: center;
            padding: 15px 0;
        }
        main {
            max-width: 800px;
            margin: 30px auto;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
        }
        h2 {
            color: #333;
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
        .chart-container {
            margin-top: 40px;
        }
        #message {
            font-weight: bold;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    
    <header>
        <h1>Registro de Citas</h1>
    </header>
    <main>
        <!-- Mostrar Mensaje de Éxito o Error -->
        <p id="message">
            <?php
            if (isset($_SESSION['message'])) {
                echo $_SESSION['message'];
                unset($_SESSION['message']);
            }
            ?>
        </p>

        <!-- Formulario de Registro -->
        <h2>Registrar Cita</h2>
        <form id="citaForm" aria-labelledby="form-title">
            <label for="fecha">Fecha</label>
            <input type="date" name="fecha" placeholder="Fecha" required>

            <label for="hora">Hora</label>
            <input type="time" name="hora" placeholder="Hora" required>

            <label for="motivo">Motivo</label>
            <input type="text" name="motivo" placeholder="Motivo" required>

            <button type="button" onclick="registrarCita()">Registrar Cita</button>
        </form>
        
        <!-- Tabla de Citas -->

        <h2>Mis Citas</h2>
        <table id="citasTable">
            <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Motivo</th>
                </tr>
            </thead>
            <tbody id="citasTable">
                <!-- Aquí se llenan las citas con JavaScript -->
            </tbody>
        </table>

    </main>
    <script>
        //Funcion para registrarCita
        async function registrarCita() {
            const fecha = document.getElementById('fecha').value;
            const hora = document.getElementById('hora').value;
            const motivo = document.getElementById('motivo').value;

            const idUsuario = <?php echo $_SESSION['idUsuario']; ?>;

            const data = {
                fecha,
                hora,
                motivo,
                idUsuario
            };

            const messageElement = document.getElementById('message');
            messageElement.textContent = "Registrando cita...";
            messageElement.style.color = "blue";

            const response = await fetch('../routes/citasRoutes.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const result = await response.json();
            messageElement.textContent = result.message;
            messageElement.style.color = result.status ==='success'? "green" : "red";

            if (result.status ==='success') {
                obtenerCitas(); // Refrescar la tabla de citas
            }
        }

        //Funcion para obtener Citas registradas

        async function obtenerCitas() {
            const idUsuario = <?php echo $_SESSION['idUsuario'];?>;

            const response = await fetch(`../routes/citasRoutes.php?idUsuario=${idUsuario}`);
            const citas = await response.json();

            const citasTable = document.getElementById('citasTable');
            citasTable.innerHTML = ''; // Limpia la tabla

            if (response.status === 'success') {
                const registros = result.data;
                registros.forEach(cita => {
                    const row = `
                        <tr>
                            <td>${cita.idCita}</td>
                            <td>${registro.fecha}</td>
                            <td>${registro.hora}</td>
                            <td>${registro.motivo}</td>
                        </tr>
                    `;
                    registrosTabla.innerHTML += row;
                });
            } else {
                alert(result.message);
            }
        }
    </script>
</body>
</html>