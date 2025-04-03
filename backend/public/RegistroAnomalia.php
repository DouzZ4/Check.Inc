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
    <title>Anomalia</title>
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
        <h1>Gestion Anomalia</h1>
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
        <h2>Registro de Anomalia</h2>
        <form id="formAnomalia" aria-labelledby="form-title">
            <label for="descripcion">Descripcion</label>
            <input type="text" id="descripcion" name="descripcion" required placeholder="Descripción de la anomalía">

            <label for="fechaHora">Fecha y Hora</label>
            <input type="datetime-local" id="fechaHora" name="fechaHora" required>

            <label for="sintomas">Sintomas</label>
            <input type="text" id="sintomas" name="sintomas" required>

            <button type="button" onclick="registrarAnomalia()">Registrar</button>
        </form>
        
        <!-- Tabla de Registros -->
        <h2>Anomalias Registradas</h2>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descripción</th>
                    <th>Fecha y Hora</th>
                    <th>Síntomas</th>
                </tr>
            </thead>
            <tbody id="registrosTabla">
                <!-- Aquí se llenan los registros con JavaScript -->
            </tbody>
        </table>
    </main>
    <script>
        // Funcion para registrar una nueva anomalía
        async function registrarAnomalia() {
            //obtener los valores del formulario
            const descripcion = document.getElementById('descripcion').value;
            const fechaHora = document.getElementById('fechaHora').value;
            const sintomas = document.getElementById('sintomas').value;
            const idUsuario = <?php echo $_SESSION['idUsuario']; ?>; // Obtener el ID del usuario de la sesión
        
            const data = {
                idUsuario,
                descripcion,
                fechaHora,
                sintomas
            };

            //mostrar mensaje de proceso
            const messageElement = document.getElementById('message'); // Elemento para mensajes
            messageElement.textContent = "Registrando datos...";
            messageElement.style.color = "blue";

            //enviar datos al backend
            const response = await fetch('../routes/AnomaliaRoutes.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            //procesar la respuesta
            const result = await response.json();

            //mostrar mensaje de resultado
            messageElement.textContent = result.message;
            messageElement.style.color = result.status === 'success' ? "green" : "red";

            //actualizar la tabla de registros
            if (result.status === 'success') {
                obtenerRegistros(); // Refrescar la tabla de registros
            }
        }

        // Función para obtener registros
        async function obtenerRegistros(){
            //obtener el id del usuario de la sesion
            const idUsuario = <?php echo $_SESSION['idUsuario']; ?>;

            //realizar la peticion al backend
            const response = await fetch(`../routes/AnomaliaRoutes.php?idUsuario=${idUsuario}`);
            const result = await response.json();

            //obtener la tabla y limpiarla
            const registrosTabla = document.getElementById('registrosTabla');
            registrosTabla.innerHTML = "";

            // verificar si la respuesta es exitosa
            if (result.status === 'success') {
                const registros = result.data;
                //recorrer los registros y agregarlos a la tabla
                registros.forEach(registro => {
                    const row = `
                        <tr>
                            <td>${registro.idAnomalia}</td>
                            <td>${registro.descripcion}</td>
                            <td>${registro.fechaHora}</td>
                            <td>${registro.sintomas}</td>
                        </tr>
                    `;
                    registrosTabla.innerHTML += row;
                });
            } else {
                alert(result.message); // Mostrar mensaje de error
            }
        }
    </script>
</body>
</html>