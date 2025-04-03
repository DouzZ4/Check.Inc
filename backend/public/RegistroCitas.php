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
</head>
<body>
<?php include __DIR__ . '/../includes/navbar.php'; ?>
<main>
    <div id="message"></div>
    <section>
        <h2 id="form-title-heading">Registrar Nueva Cita</h2>
        <form id="formCita" onsubmit="event.preventDefault(); manejarSubmitFormulario();">
            <input type="hidden" id="editId" value="">
            <div>
                <label for="fecha">Fecha:</label>
                <input type="date" id="fecha" name="fecha" required>
            </div>
            <div>
                <label for="hora">Hora:</label>
                <input type="time" id="hora" name="hora" required>
            </div>
            <div>
                <label for="motivo">Motivo:</label>
                <input type="text" id="motivo" name="motivo" required>
            </div>
            <div id="form-buttons">
                <button type="submit" id="submitButton">Registrar</button>
                <button type="button" id="cancelButton" class="cancel-button" onclick="cancelarEdicion()" style="display: none;">Cancelar Edición</button>
            </div>
        </form>
    </section>
    <section>
        <h2>Listado de Citas</h2>
        <table>
            <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Motivo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody id="tablaCitas"></tbody>
        </table>
    </section>
</main>
<script>
    const idUsuarioActual = <?php echo json_encode($idUsuarioActual); ?>;
    const apiUrl = '../routes/citasRoutes.php';
    const form = document.getElementById('formCita');
    const editIdInput = document.getElementById('editId');
    const submitButton = document.getElementById('submitButton');
    const cancelButton = document.getElementById('cancelButton');
    const tablaCitas = document.getElementById('tablaCitas');
    const messageElement = document.getElementById('message');

    function mostrarMensaje(texto, tipo = 'info') {
        messageElement.textContent = texto;
        messageElement.className = `message-${tipo}`;
    }

    function resetearFormulario() {
        form.reset();
        editIdInput.value = "";
        submitButton.textContent = "Registrar";
        cancelButton.style.display = 'none';
    }

    function manejarSubmitFormulario() {
        const idParaEditar = editIdInput.value;
        if (idParaEditar) {
            actualizarCita(parseInt(idParaEditar));
        } else {
            crearCita();
        }
    }

    async function crearCita() {
        const data = {
            idUsuario: idUsuarioActual,
            fecha: document.getElementById('fecha').value,
            hora: document.getElementById('hora').value,
            motivo: document.getElementById('motivo').value
        };
        mostrarMensaje("Registrando cita...", "info");
        try {
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            if (result.status === 'success') {
                mostrarMensaje(result.message, "success");
                resetearFormulario();
                cargarCitas();
            } else {
                mostrarMensaje(result.message, "error");
            }
        } catch (error) {
            mostrarMensaje("Error al registrar la cita.", "error");
        }
    }

    async function cargarCitas() {
        try {
            const response = await fetch(`${apiUrl}?idUsuario=${idUsuarioActual}`);
            const result = await response.json();
            tablaCitas.innerHTML = '';
            if (result.status === 'success') {
                result.data.forEach(cita => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${cita.fecha}</td>
                        <td>${cita.hora}</td>
                        <td>${cita.motivo}</td>
                        <td>
                            <button onclick="prepararEdicion(${cita.idCita}, '${cita.fecha}', '${cita.hora}', '${cita.motivo}')">Editar</button>
                            <button onclick="eliminarCita(${cita.idCita})">Eliminar</button>
                        </td>
                    `;
                    tablaCitas.appendChild(row);
                });
            } else {
                mostrarMensaje(result.message, "error");
            }
        } catch (error) {
            mostrarMensaje("Error al cargar las citas.", "error");
        }
    }

    function prepararEdicion(idCita, fecha, hora, motivo) {
        editIdInput.value = idCita;
        document.getElementById('fecha').value = fecha;
        document.getElementById('hora').value = hora;
        document.getElementById('motivo').value = motivo;
        submitButton.textContent = "Actualizar";
        cancelButton.style.display = 'inline-block';
    }

    async function actualizarCita(idCita) {
        const data = {
            fecha: document.getElementById('fecha').value,
            hora: document.getElementById('hora').value,
            motivo: document.getElementById('motivo').value
        };
        mostrarMensaje("Actualizando cita...", "info");
        try {
            const response = await fetch(`${apiUrl}?idCita=${idCita}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            if (result.status === 'success') {
                mostrarMensaje(result.message, "success");
                resetearFormulario();
                cargarCitas();
            } else {
                mostrarMensaje(result.message, "error");
            }
        } catch (error) {
            mostrarMensaje("Error al actualizar la cita.", "error");
        }
    }

    async function eliminarCita(idCita) {
        if (confirm("¿Estás seguro de eliminar esta cita?")) {
            mostrarMensaje("Eliminando cita...", "info");
            try {
                const response = await fetch(`${apiUrl}?idCita=${idCita}`, { method: 'DELETE' });
                const result = await response.json();
                if (result.status === 'success') {
                    mostrarMensaje(result.message, "success");
                    cargarCitas();
                } else {
                    mostrarMensaje(result.message, "error");
                }
            } catch (error) {
                mostrarMensaje("Error al eliminar la cita.", "error");
            }
        }
    }

    window.onload = cargarCitas;
</script>
</body>
</html>