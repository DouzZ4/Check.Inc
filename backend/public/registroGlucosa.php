<?php
// Necesario para la autenticación y obtener el ID de usuario
session_start();
if (!isset($_SESSION['idUsuario'])) {
    // $_SESSION['message'] = "Por favor, inicia sesión para acceder a la página."; // Mensaje se gestiona en login.php
    header('Location: login.php'); // Redirige si no hay sesión
    exit;
}
// Guardar idUsuario para usar en JS
$idUsuarioActual = $_SESSION['idUsuario'];
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro y Consulta de Glucosa</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <link rel="stylesheet" href="./css/styles.css">
    <link rel="stylesheet" href="./css/navbar.css"> </head>
<body>

    <nav class="navbar">
        <div class="navbar-container">
            <div class="navbar-branding">
                <div id="logo-placeholder">Logo Aquí</div>
                <h1>Control Glucosa</h1>
            </div>
            <div class="navbar-user-info">
                </div>
        </div>
    </nav>
    <main>
        <div id="message">
             <?php
              // Mensaje inicial de sesión (si existe, útil después del login)
              if (isset($_SESSION['message'])) {
                  $message_class = 'message-info'; // O detectar tipo de mensaje si se guarda en sesión
                  echo '<div id="message-initial" class="' . $message_class . '">' . htmlspecialchars($_SESSION['message']) . '</div>';
                  unset($_SESSION['message']);
              }
             ?>
        </div>

        <section aria-labelledby="form-title-heading">
             <h2 id="form-title-heading">Registrar Nueva Medición</h2>
             <form id="formGlucosa" aria-describedby="message" onsubmit="event.preventDefault(); manejarSubmitFormulario();"> <input type="hidden" id="editId" value="">

                 <div>
                     <label for="nivelGlucosa">Nivel de Glucosa (mg/dL):</label>
                     <input type="number" id="nivelGlucosa" name="nivelGlucosa" placeholder="Ej: 95" required min="1">
                 </div>

                 <div>
                     <label for="fechaHora">Fecha y Hora:</label>
                     <input type="datetime-local" id="fechaHora" name="fechaHora" required>
                 </div>

                 <div id="form-buttons">
                     <button type="submit" id="submitButton">Registrar</button>
                     <button type="button" id="cancelButton" class="cancel-button" onclick="cancelarEdicion()" style="display: none;">Cancelar Edición</button>
                 </div>
             </form>
        </section>

        <section aria-labelledby="registros-heading">
            <h2 id="registros-heading">Registros Anteriores</h2>
            <div style="overflow-x: auto;"> <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nivel (mg/dL)</th>
                            <th>Fecha y Hora</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="registrosTabla">
                        </tbody>
                </table>
            </div>
        </section>

        <section aria-labelledby="grafico-heading">
            <div class="chart-container">
                <h2 id="grafico-heading">Gráfico de Glucosa</h2>
                <canvas id="graficoGlucosa" aria-label="Gráfico de evolución del nivel de glucosa"></canvas>
            </div>
        </section>
    </main>
    <script>
        // --- Variables Globales y Referencias a Elementos ---
        const idUsuarioActual = <?php echo json_encode($idUsuarioActual); ?>;
        // Asegúrate que esta ruta sea correcta desde donde se ejecuta el JS en el navegador
        const apiUrl = '../routes/glucosaRoutes.php';
        const form = document.getElementById('formGlucosa');
        const formTitle = document.getElementById('form-title-heading'); // ID corregido
        const nivelGlucosaInput = document.getElementById('nivelGlucosa');
        const fechaHoraInput = document.getElementById('fechaHora');
        const editIdInput = document.getElementById('editId');
        const submitButton = document.getElementById('submitButton');
        const cancelButton = document.getElementById('cancelButton');
        const messageElement = document.getElementById('message');
        const registrosTablaBody = document.getElementById('registrosTabla');
        let chartInstance = null; // Para gestionar la instancia del gráfico

        // --- FUNCIONES DE LA INTERFAZ ---

        function mostrarMensaje(texto, tipo = 'info') {
             // Limpiar contenido anterior y clases
             messageElement.innerHTML = '';
             messageElement.className = ''; // Limpia clases previas

             // Crear el div del mensaje si hay texto
             if (texto) {
                const messageDiv = document.createElement('div');
                messageDiv.textContent = texto;
                messageDiv.className = `message-${tipo}`; // Aplica clase CSS para color
                messageElement.appendChild(messageDiv);
                messageElement.style.display = 'block'; // Asegura que sea visible
             } else {
                 messageElement.style.display = 'none'; // Oculta si no hay mensaje
             }
        }

        function resetearFormulario() {
            form.reset();
            editIdInput.value = "";
            formTitle.textContent = "Registrar Nueva Medición";
            submitButton.textContent = "Registrar";
            // submitButton.onclick ya no es necesario si usamos onsubmit en el form
            cancelButton.style.display = 'none';
             // Limpiar mensaje después de un momento
             setTimeout(() => {
                 if (messageElement.textContent === "Edición cancelada.") {
                     mostrarMensaje(''); // Limpia el mensaje de cancelación
                 }
             }, 3000);
        }

        function prepararFormularioEdicion(registro) {
             formTitle.textContent = `Editando Registro ID: ${registro.idGlucosa}`;
             editIdInput.value = registro.idGlucosa;
             nivelGlucosaInput.value = registro.nivelGlucosa;

             try {
                 // Asume formato 'YYYY-MM-DD HH:MM:SS' de la BD
                 const fechaBD = registro.fechaHora.replace(' ', 'T');
                 // Validar si la fecha es interpretable antes de asignarla
                  if (!isNaN(new Date(fechaBD).getTime())) {
                       // datetime-local necesita 'YYYY-MM-DDTHH:mm'
                      fechaHoraInput.value = fechaBD.substring(0, 16);
                  } else {
                      console.warn("Formato de fecha recibido no compatible con datetime-local:", registro.fechaHora);
                      fechaHoraInput.value = ''; // O manejar de otra forma
                  }

             } catch(e) {
                 console.error("Error procesando fecha para input:", e);
                 fechaHoraInput.value = ''; // Limpiar en caso de error
             }

             submitButton.textContent = "Actualizar Registro";
             cancelButton.style.display = 'inline-block';
             nivelGlucosaInput.focus(); // Poner foco en el primer campo editable
             // window.scrollTo(0, 0); // Descomentar si quieres scroll automático
        }

        function cancelarEdicion() {
             resetearFormulario();
             mostrarMensaje("Edición cancelada.", "info");
        }

        // Decide si llamar a crear o actualizar (llamado desde onsubmit del form)
        function manejarSubmitFormulario() {
            const idParaEditar = editIdInput.value;
            if (idParaEditar) {
                actualizarRegistro(parseInt(idParaEditar));
            } else {
                crearRegistro();
            }
        }

        // --- FUNCIONES DE COMUNICACIÓN CON EL BACKEND (API) ---

        async function crearRegistro() {
             if (!nivelGlucosaInput.value || !fechaHoraInput.value) {
                 mostrarMensaje("Por favor, completa Nivel de Glucosa y Fecha/Hora.", "error");
                 return;
             }
            const data = {
                idUsuario: idUsuarioActual,
                nivelGlucosa: nivelGlucosaInput.value,
                fechaHora: fechaHoraInput.value
            };
            mostrarMensaje("Registrando...", "info");
            submitButton.disabled = true; // Deshabilitar botón mientras procesa

            try {
                const response = await fetch(apiUrl, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
                const result = await response.json();

                if (response.status === 201 && result.status === 'success') { // Chequear código 201 Created
                    mostrarMensaje(result.message, "success");
                    resetearFormulario();
                    obtenerRegistrosYActualizarUI();
                } else {
                     mostrarMensaje(`Error al registrar: ${result.message || 'Respuesta no válida'}`, "error");
                }
            } catch (error) {
                console.error("Error en fetch POST:", error);
                mostrarMensaje(`Error de red o conexión al registrar. Intenta de nuevo.`, "error");
            } finally {
                 submitButton.disabled = false; // Rehabilitar botón
            }
        }

        async function obtenerRegistrosYActualizarUI() {
             // No mostramos 'Cargando...' cada vez para evitar parpadeo si ya hay datos
             // mostrarMensaje("Cargando registros...", "info");
             try {
                 const response = await fetch(`${apiUrl}?idUsuario=${idUsuarioActual}`, { method: 'GET' });
                 const result = await response.json();
                 registrosTablaBody.innerHTML = ''; // Limpiar tabla

                 if (response.ok && result.status === 'success') {
                     if (result.data.length === 0) {
                         mostrarMensaje("Aún no tienes registros.", "info");
                         registrosTablaBody.innerHTML = '<tr><td colspan="4">No hay registros para mostrar.</td></tr>';
                     } else {
                          // Limpiar mensaje solo si antes había un mensaje de 'no hay registros' o similar
                          if(messageElement.textContent === "Aún no tienes registros." || messageElement.textContent === "Registros cargados."){
                               mostrarMensaje(''); // Limpiar mensaje de estado
                          }
                     }
                     const registros = result.data;
                     registros.forEach(registro => {
                         const row = document.createElement('tr');
                         row.innerHTML = `
                             <td>${registro.idGlucosa}</td>
                             <td>${registro.nivelGlucosa}</td>
                             <td>${registro.fechaHora ? new Date(registro.fechaHora.replace(' ','T')).toLocaleString('es-CO') : 'Fecha inválida'}</td>
                             <td>
                                 <button class="action-button edit-button" onclick="iniciarEdicion(${registro.idGlucosa})">Editar</button>
                                 <button class="action-button delete-button" onclick="confirmarEliminacion(${registro.idGlucosa})">Eliminar</button>
                             </td>
                         `;
                         registrosTablaBody.appendChild(row);
                     });
                     actualizarGrafico(registros);
                 } else {
                     mostrarMensaje(`Error al cargar registros: ${result.message || 'Respuesta no válida'}`, "error");
                 }
             } catch (error) {
                 console.error("Error en fetch GET (todos):", error);
                 mostrarMensaje(`Error de red o conexión al cargar registros. Recarga la página.`, "error");
                 registrosTablaBody.innerHTML = '<tr><td colspan="4">Error al cargar datos.</td></tr>';
             }
        }

        async function iniciarEdicion(idGlucosa) {
             mostrarMensaje(`Cargando datos del registro ${idGlucosa}...`, "info");
            try {
                 const response = await fetch(`${apiUrl}?idGlucosa=${idGlucosa}`, { method: 'GET' });
                 const result = await response.json();
                 if (response.ok && result.status === 'success') {
                     prepararFormularioEdicion(result.data);
                     mostrarMensaje(`Editando registro ID: ${idGlucosa}.`, "info");
                 } else {
                     mostrarMensaje(`Error al cargar registro ${idGlucosa}: ${result.message || 'No encontrado'}`, "error");
                 }
            } catch (error) {
                 console.error("Error en fetch GET (uno):", error);
                 mostrarMensaje(`Error de red o conexión al cargar registro ${idGlucosa}.`, "error");
            }
        }

        async function actualizarRegistro(idGlucosa) {
             if (!nivelGlucosaInput.value || !fechaHoraInput.value) {
                 mostrarMensaje("Por favor, completa Nivel de Glucosa y Fecha/Hora.", "error");
                 return;
             }
            const data = {
                nivelGlucosa: nivelGlucosaInput.value,
                fechaHora: fechaHoraInput.value
            };
            mostrarMensaje(`Actualizando registro ${idGlucosa}...`, "info");
            submitButton.disabled = true;
            cancelButton.disabled = true;

             try {
                const response = await fetch(`${apiUrl}?idGlucosa=${idGlucosa}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
                const result = await response.json();

                 if (response.ok && (result.status === 'success' || result.status === 'info')) {
                    mostrarMensaje(result.message, result.status);
                    resetearFormulario();
                    obtenerRegistrosYActualizarUI();
                } else {
                     mostrarMensaje(`Error al actualizar: ${result.message || 'Respuesta no válida'}`, "error");
                }
             } catch (error) {
                 console.error("Error en fetch PUT:", error);
                 mostrarMensaje(`Error de red o conexión al actualizar. Intenta de nuevo.`, "error");
             } finally {
                  submitButton.disabled = false;
                  cancelButton.disabled = false;
             }
        }

        function confirmarEliminacion(idGlucosa) {
            // Podrías usar un modal más elegante aquí en lugar de confirm()
            if (confirm(`¿Estás seguro de que deseas eliminar el registro ID: ${idGlucosa}? Esta acción no se puede deshacer.`)) {
                eliminarRegistro(idGlucosa);
            }
        }

        async function eliminarRegistro(idGlucosa) {
            mostrarMensaje(`Eliminando registro ${idGlucosa}...`, "info");
            try {
                 const response = await fetch(`${apiUrl}?idGlucosa=${idGlucosa}`, {
                     method: 'DELETE'
                 });
                 const result = await response.json();

                 if (response.ok && result.status === 'success') {
                     mostrarMensaje(result.message, "success");
                     if (parseInt(editIdInput.value) === idGlucosa) {
                         resetearFormulario();
                     }
                     obtenerRegistrosYActualizarUI();
                 } else {
                       // Manejar info (no encontrado) o error
                       mostrarMensaje(`Error al eliminar: ${result.message || 'Respuesta no válida'}`, (response.status === 404 ? "info" : "error"));
                 }
             } catch(error) {
                 console.error("Error en fetch DELETE:", error);
                 mostrarMensaje(`Error de red o conexión al eliminar. Intenta de nuevo.`, "error");
             }
        }

        // --- GRÁFICO ---
        function actualizarGrafico(registros) {
             const ctx = document.getElementById('graficoGlucosa').getContext('2d');
             // Destruir gráfico anterior si existe
             if (chartInstance) {
                 chartInstance.destroy();
             }
             // Si no hay registros, no mostrar gráfico o mostrar mensaje
             if (!registros || registros.length === 0) {
                // Podrías limpiar el canvas o mostrar un mensaje en su lugar
                 ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height); // Limpia
                 // Opcional: Mostrar texto en el canvas
                 // ctx.textAlign = 'center';
                 // ctx.fillText('No hay datos para mostrar en el gráfico.', ctx.canvas.width / 2, ctx.canvas.height / 2);
                 return;
             }

            // Ordenar por fecha ascendente para la línea de tiempo
            const registrosOrdenados = [...registros].sort((a, b) => new Date(a.fechaHora.replace(' ','T')) - new Date(b.fechaHora.replace(' ','T')));

            const labels = registrosOrdenados.map(r => {
                 try { return new Date(r.fechaHora.replace(' ','T')).toLocaleString('es-CO', { day: 'numeric', month: 'short', hour: '2-digit', minute:'2-digit', hour12: true }); } catch { return r.fechaHora; }
             });
            const dataPoints = registrosOrdenados.map(r => r.nivelGlucosa);

            chartInstance = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Nivel de Glucosa (mg/dL)',
                        data: dataPoints,
                        borderColor: '#f45501', // Naranja
                        backgroundColor: 'rgba(244, 85, 1, 0.1)', // Naranja translúcido
                        borderWidth: 2.5, // Un poco más gruesa
                        tension: 0.1,
                        pointBackgroundColor: '#3058a6', // Puntos en Azul
                        pointBorderColor: '#ffffff',
                        pointRadius: 4,
                        pointHoverRadius: 6
                    }]
                },
                options: {
                     responsive: true,
                     maintainAspectRatio: false, // Permitir ajustar altura con CSS si es necesario
                     plugins: {
                         title: { display: true, text: 'Evolución del Nivel de Glucosa', font: { size: 16 } },
                         legend: { display: false },
                         tooltip: {
                              callbacks: {
                                   title: function(tooltipItems) {
                                        // Mostrar fecha más completa en tooltip
                                        const index = tooltipItems[0].dataIndex;
                                        try { return new Date(registrosOrdenados[index].fechaHora.replace(' ','T')).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short'}); } catch { return labels[index]; }
                                   }
                              }
                         }
                     },
                     scales: {
                         x: { title: { display: true, text: 'Fecha y Hora' } },
                         y: { title: { display: true, text: 'Nivel (mg/dL)' }, beginAtZero: false }
                     }
                 }
             });
         }

        // --- INICIALIZACIÓN ---
        window.onload = obtenerRegistrosYActualizarUI;

    </script>
    </body>
</html>