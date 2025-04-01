<?php session_start(); ?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro y Consulta de Glucosa</title>
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
    </style>
</head>
<body>
    <header>
        <h1>Registro y Consulta de Glucosa</h1>
    </header>
    <main>
        <!-- Mostrar Mensaje de Éxito o Error -->
        <?php
        if (isset($_SESSION['message'])) {
            echo "<p style='color: green; font-weight: bold;'>{$_SESSION['message']}</p>";
            unset($_SESSION['message']);
        }
        ?>

        <!-- Formulario de Registro -->
        <h2>Registrar Nueva Medición</h2>
        <form id="formGlucosa">
            <input type="number" id="nivelGlucosa" placeholder="Nivel de Glucosa (mg/dL)" required min="1">
            <input type="datetime-local" id="fechaHora" required>
            <button type="button" onclick="registrarGlucosa()">Registrar</button>
        </form>

        <!-- Tabla de Registros -->
        <h2>Registros Anteriores</h2>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nivel</th>
                    <th>Fecha</th>
                </tr>
            </thead>
            <tbody id="registrosTabla">
                <!-- Aquí se llenan los registros con JavaScript -->
            </tbody>
        </table>

        <!-- Gráfico -->
        <div class="chart-container">
            <h2>Gráfico de Glucosa</h2>
            <canvas id="graficoGlucosa"></canvas>
        </div>
    </main>
    <script>
        // Función para registrar nueva medición
        async function registrarGlucosa() {
            const nivelGlucosa = document.getElementById('nivelGlucosa').value;
            const fechaHora = document.getElementById('fechaHora').value;

            const data = {
                idUsuario: 1, // Cambia según tu lógica de sesión
                nivelGlucosa,
                fechaHora
            };

            const response = await fetch('../routes/glucosaRoutes.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const result = await response.json();
            alert(result.message);
            if (result.status === 'success') {
                obtenerRegistros(); // Refrescar la tabla de registros
            }
        }

        // Función para obtener registros
        async function obtenerRegistros() {
            const idUsuario = 1; // Cambia según tu lógica de sesión

            const response = await fetch(`../routes/glucosaRoutes.php?idUsuario=${idUsuario}`);
            const result = await response.json();

            const registrosTabla = document.getElementById('registrosTabla');
            registrosTabla.innerHTML = ''; // Limpiar tabla

            if (result.status === 'success') {
                const registros = result.data;
                registros.forEach(registro => {
                    const row = `
                        <tr>
                            <td>${registro.idGlucosa}</td>
                            <td>${registro.nivelGlucosa}</td>
                            <td>${registro.fechaHora}</td>
                        </tr>
                    `;
                    registrosTabla.innerHTML += row;
                });

                // Actualizar gráfico
                actualizarGrafico(registros);
            } else {
                alert(result.message);
            }
        }

        // Función para actualizar el gráfico
        function actualizarGrafico(registros) {
            const fechas = registros.map(r => r.fechaHora);
            const niveles = registros.map(r => r.nivelGlucosa);

            const ctx = document.getElementById('graficoGlucosa').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: fechas,
                    datasets: [{
                        label: 'Nivel de Glucosa (mg/dL)',
                        data: niveles,
                        borderColor: 'rgba(75, 192, 192, 1)',
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        title: {
                            display: true,
                            text: 'Evolución del Nivel de Glucosa'
                        }
                    },
                    scales: {
                        x: {
                            title: { display: true, text: 'Fecha y Hora' }
                        },
                        y: {
                            title: { display: true, text: 'Nivel de Glucosa (mg/dL)' }
                        }
                    }
                }
            });
        }

        // Cargar registros al cargar la página
        window.onload = obtenerRegistros;
    </script>
</body>
</html>