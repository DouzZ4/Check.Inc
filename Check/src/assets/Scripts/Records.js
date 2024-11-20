// Funciones para manejar modales
function abrirModal(tipo) {
    document.getElementById(`modal${capitalizeFirstLetter(tipo)}`).style.display = "block";
}

function cerrarModal(tipo) {
    document.getElementById(`modal${capitalizeFirstLetter(tipo)}`).style.display = "none";
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

// Validación básica para cada formulario
document.getElementById('formMedicamentos').addEventListener('submit', function(e) {
    e.preventDefault();
    alert('Medicamento registrado');
    this.reset();
    cerrarModal('medicamentos');
});

document.getElementById('formActividades').addEventListener('submit', function(e) {
    e.preventDefault();
    alert('Actividad registrada');
    this.reset();
    cerrarModal('actividades');
});

document.getElementById('formCitas').addEventListener('submit', function(e) {
    e.preventDefault();
    alert('Cita registrada');
    this.reset();
    cerrarModal('citas');
});

function abrirModal(modalId) {
    document.getElementById(`modal${capitalizeFirstLetter(modalId)}`).style.display = 'block';
}

function cerrarModal(modalId) {
    document.getElementById(`modal${capitalizeFirstLetter(modalId)}`).style.display = 'none';
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

// Ejemplo de gestión de formularios (Glucosa)
document.getElementById('formGlucosa').addEventListener('submit', function (e) {
    e.preventDefault();
    const nivel = document.getElementById('glucosaNivel').value;
    const fecha = document.getElementById('glucosaFecha').value;
    const hora = document.getElementById('glucosaHora').value;
    console.log(`Nivel: ${nivel}, Fecha: ${fecha}, Hora: ${hora}`);
    cerrarModal('glucosa');
});
