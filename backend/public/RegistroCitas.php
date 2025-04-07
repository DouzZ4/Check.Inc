<?php
session_start();
if (!isset($_SESSION['idUsuario'])) {
    header('Location: login.php');
    exit;
}
$idUsuarioActual = $_SESSION['idUsuario'];
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Citas</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link rel="stylesheet" href="./css/styles.css">
    <link rel="stylesheet" href="./css/navbar.css">
    <link rel="stylesheet" href="./css/form.css">
</head>
<body>
    <?php include __DIR__ . '/../includes/navbar.php'; // Incluir Navbar ?>

    <main class="main-container citas-container"> <h1>Gestión de Citas Médicas</h1>

        <div id="message" class="message-box-placeholder">
             <?php
              // Mensaje inicial de sesión (si existe)
              if (isset($_SESSION['message'])) {
                  $message_type = $_SESSION['message_type'] ?? 'info';
                  $message_class = 'message-' . htmlspecialchars($message_type);
                  $msg = htmlspecialchars($_SESSION['message']);
                  // Imprimir directamente la caja de mensaje
                  echo "<div class='message-box {$message_class}'>{$msg}</div>";
                  unset($_SESSION['message']);
                  unset($_SESSION['message_type']);
              }
             ?>
        </div>

        <section class="form-section card" aria-labelledby="form-title-heading">
             <h2 id="form-title-heading">Registrar Nueva Cita</h2>
             <form id="formCita" onsubmit="event.preventDefault(); manejarSubmitFormulario();">
                 <input type="hidden" id="editId" value="">

                 <div class="form-group">
                     <label for="fecha">Fecha:</label>
                     <input type="date" id="fecha" name="fecha" required>
                 </div>

                 <div class="form-group">
                     <label for="hora">Hora:</label>
                     <input type="time" id="hora" name="hora" required>
                 </div>

                 <div class="form-group">
                     <label for="motivo">Motivo:</label>
                     <textarea id="motivo" name="motivo" rows="3" placeholder="Ej: Control anual, Examen de sangre..." required></textarea>
                 </div>

                 <div id="form-buttons" class="form-group">
                     <button type="submit" id="submitButton" class="button">Registrar</button>
                     <button type="button" id="cancelButton" class="button button-secondary cancel-button" onclick="cancelarEdicion()" style="display: none;">Cancelar Edición</button>
                 </div>
             </form>
        </section>

        <section class="table-section card" aria-labelledby="citas-heading">
            <h2 id="citas-heading">Listado de Citas</h2>
            <div id="loadingIndicator" style="display: none; text-align: center; padding: 15px;">Cargando citas...</div>
            <div class="table-wrapper" style="overflow-x: auto;">
                <table>
                    <thead>
                        <tr>
                            <th>Fecha</th>
                            <th>Hora</th>
                            <th>Motivo</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="tablaCitas">
                        <tr><td colspan="4" style="text-align:center;">Cargando...</td></tr>
                    </tbody>
                </table>
            </div>
        </section>
    </main>

    <script>
        // --- Variables Globales y Referencias ---
        const idUsuarioActual = <?php echo json_encode($idUsuarioActual); ?>;
        const apiUrl = '../routes/citasRoutes.php'; // <<< ¡¡¡ASEGÚRATE DE TENER ESTE ARCHIVO EN routes!!!
        const form = document.getElementById('formCita');
        const formTitle = document.getElementById('form-title-heading');
        const fechaInput = document.getElementById('fecha');
        const horaInput = document.getElementById('hora');
        const motivoInput = document.getElementById('motivo');
        const editIdInput = document.getElementById('editId');
        const submitButton = document.getElementById('submitButton');
        const cancelButton = document.getElementById('cancelButton');
        const tablaCitasBody = document.getElementById('tablaCitas'); // Corregido el ID usado
        const messageElement = document.getElementById('message');
        const loadingIndicator = document.getElementById('loadingIndicator');
        let editando = false; // Flag para saber si estamos en modo edición

        // --- FUNCIONES DE UI ---

        function mostrarMensaje(texto, tipo = 'info', duracion = 4000) {
            // Limpiar mensajes anteriores
            messageElement.innerHTML = '';
            messageElement.className = 'message-box-placeholder'; // Limpia clases anteriores

            if (texto) {
                const messageDiv = document.createElement('div');
                messageDiv.textContent = texto;
                messageDiv.className = `message-box message-${tipo}`; // Aplica clase CSS
                messageElement.appendChild(messageDiv);
                messageElement.style.display = 'block';

                // Ocultar automáticamente después de 'duracion' ms
                if (duracion > 0) {
                    setTimeout(() => {
                         if (messageDiv.parentNode === messageElement) { // Evita error si se limpió antes
                            messageElement.removeChild(messageDiv);
                            if (messageElement.children.length === 0) {
                                messageElement.style.display = 'none';
                            }
                         }
                    }, duracion);
                }
            } else {
                 messageElement.style.display = 'none';
            }
        }

        function resetearFormulario() {
            form.reset();
            editIdInput.value = "";
            editando = false;
            formTitle.textContent = "Registrar Nueva Cita";
            submitButton.textContent = "Registrar";
            cancelButton.style.display = 'none';
            form.classList.remove('editing'); // Quitar clase de modo edición
            submitButton.disabled = false;
            cancelButton.disabled = false;
        }

        function prepararEdicion(idCita, fecha, hora, motivo) {
            editIdInput.value = idCita;
            fechaInput.value = fecha; // HTML5 type="date" espera YYYY-MM-DD
            horaInput.value = hora;   // HTML5 type="time" espera HH:MM o HH:MM:SS
            motivoInput.value = motivo;

            editando = true;
            formTitle.textContent = `Editando Cita ID: ${idCita}`;
            submitButton.textContent = "Actualizar Cita";
            cancelButton.style.display = 'inline-block';
            form.classList.add('editing'); // Añadir clase para posible estilo
            window.scrollTo({ top: form.offsetTop - 80, behavior: 'smooth' }); // Scroll suave al formulario
            fechaInput.focus();
        }

        function cancelarEdicion() {
            resetearFormulario();
            mostrarMensaje("Edición cancelada.", "info");
        }

        function manejarSubmitFormulario() {
            const idParaEditar = editIdInput.value;
            if (editando && idParaEditar) { // Usar flag 'editando'
                actualizarCita(parseInt(idParaEditar));
            } else {
                crearCita();
            }
        }

        function mostrarLoading(mostrar = true) {
            loadingIndicator.style.display = mostrar ? 'block' : 'none';
        }

        // --- FUNCIONES API (CRUD) ---

        async function crearCita() {
            if (!fechaInput.value || !horaInput.value || !motivoInput.value.trim()) {
                mostrarMensaje("Completa Fecha, Hora y Motivo.", "error");
                return;
            }
            const data = {
                idUsuario: idUsuarioActual,
                fecha: fechaInput.value,
                hora: horaInput.value,
                motivo: motivoInput.value.trim()
            };
            mostrarMensaje("Registrando...", "info", 0); // Mensaje persistente
            submitButton.disabled = true;

            try {
                const response = await fetch(apiUrl, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                    body: JSON.stringify(data)
                });
                const result = await response.json();
                if (!response.ok || result.status !== 'success') {
                     throw new Error(result.message || `Error HTTP ${response.status}`);
                }
                mostrarMensaje(result.message || "Cita registrada.", "success");
                resetearFormulario();
                cargarCitas(); // Recargar lista
            } catch (error) {
                console.error("Error en crearCita:", error);
                mostrarMensaje(`Error al registrar: ${error.message}`, "error");
            } finally {
                 submitButton.disabled = false;
            }
        }

        async function cargarCitas() {
            mostrarLoading(true);
            tablaCitasBody.innerHTML = ''; // Limpiar tabla antes de cargar
            try {
                const response = await fetch(`${apiUrl}?idUsuario=${idUsuarioActual}`);
                const result = await response.json();
                if (!response.ok || result.status !== 'success') {
                    throw new Error(result.message || `Error HTTP ${response.status}`);
                }

                if (result.data.length === 0) {
                    tablaCitasBody.innerHTML = '<tr><td colspan="4" style="text-align:center;">No tienes citas registradas.</td></tr>';
                } else {
                    result.data.forEach(cita => {
                        const row = document.createElement('tr');
                        // Formatear fecha y hora para mostrar (opcional)
                        const fechaFormato = cita.fecha ? new Date(cita.fecha + 'T00:00:00').toLocaleDateString('es-CO', { year: 'numeric', month: 'long', day: 'numeric' }) : 'N/A';
                        const horaFormato = cita.hora ? cita.hora.substring(0,5) : 'N/A'; // Mostrar solo HH:MM

                        row.innerHTML = `
                            <td>${fechaFormato}</td>
                            <td>${horaFormato}</td>
                            <td>${htmlspecialchars(cita.motivo || '')}</td>
                            <td>
                                <button class="action-button edit-button" onclick="prepararEdicion(${cita.idCita}, '${cita.fecha}', '${cita.hora}', \`${htmlspecialchars(cita.motivo || '', true)}\`)">Editar</button>
                                <button class="action-button delete-button" onclick="confirmarEliminacion(${cita.idCita})">Eliminar</button>
                            </td>
                        `;
                        tablaCitasBody.appendChild(row);
                    });
                }
                 // Limpiar mensaje de 'cargando' u otros mensajes previos si la carga fue exitosa
                 if (messageElement.textContent === "Cargando citas...") mostrarMensaje('');

            } catch (error) {
                console.error("Error en cargarCitas:", error);
                mostrarMensaje(`Error al cargar citas: ${error.message}`, "error");
                tablaCitasBody.innerHTML = `<tr><td colspan="4" style="text-align:center;">Error al cargar datos.</td></tr>`;
            } finally {
                mostrarLoading(false);
            }
        }

        async function actualizarCita(idCita) {
             if (!fechaInput.value || !horaInput.value || !motivoInput.value.trim()) {
                mostrarMensaje("Completa Fecha, Hora y Motivo.", "error");
                return;
            }
            const data = {
                fecha: fechaInput.value,
                hora: horaInput.value,
                motivo: motivoInput.value.trim()
            };
            mostrarMensaje("Actualizando...", "info", 0);
            submitButton.disabled = true;
            cancelButton.disabled = true;

            try {
                const response = await fetch(`${apiUrl}?idCita=${idCita}`, { // Asume que PUT necesita idCita en URL
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                    body: JSON.stringify(data)
                });
                const result = await response.json();
                 if (!response.ok || (result.status !== 'success' && result.status !== 'info')) { // Aceptar success o info
                     throw new Error(result.message || `Error HTTP ${response.status}`);
                 }
                mostrarMensaje(result.message || "Cita actualizada.", result.status); // Usa status ('success' o 'info')
                resetearFormulario();
                cargarCitas();
            } catch (error) {
                console.error("Error en actualizarCita:", error);
                mostrarMensaje(`Error al actualizar: ${error.message}`, "error");
            } finally {
                 submitButton.disabled = false;
                 cancelButton.disabled = false;
            }
        }

        function confirmarEliminacion(idCita) {
            // Podrías usar un modal personalizado para confirmación
            if (confirm(`¿Estás realmente seguro de eliminar la cita ID: ${idCita}?`)) {
                eliminarCita(idCita);
            }
        }

        async function eliminarCita(idCita) {
            mostrarMensaje("Eliminando...", "info", 0);
            try {
                const response = await fetch(`${apiUrl}?idCita=${idCita}`, { method: 'DELETE' }); // Asume DELETE necesita idCita
                const result = await response.json();
                 if (!response.ok || result.status !== 'success') {
                      // Si no se encontró (info) o hubo otro error
                     throw new Error(result.message || `Error HTTP ${response.status}`);
                 }
                mostrarMensaje(result.message || "Cita eliminada.", "success");
                if (parseInt(editIdInput.value) === idCita) { // Si se estaba editando la que se borró
                    resetearFormulario();
                }
                cargarCitas();
            } catch(error) {
                console.error("Error en eliminarCita:", error);
                mostrarMensaje(`Error al eliminar: ${error.message}`, "error");
            }
        }

         // --- Helper para sanitizar HTML en JS (simple) ---
         function htmlspecialchars(str, isAttribute = false) {
             if (typeof str !== 'string') return str;
             const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
             const pattern = isAttribute ? /[&<>"']/g : /[&<>]/g; // Ser más estricto para atributos
             return str.replace(pattern, (m) => map[m]);
         }


        // --- INICIALIZACIÓN ---
        window.onload = cargarCitas;

    </script>
</body>
</html>