// Array para almacenar los medicamentos registrados
let medicamentos = [];

// Función para registrar un nuevo medicamento
function registrarMedicamento() {
    const nombre = document.getElementById('nombre').value;
    const descripcion = document.getElementById('descripcion').value;

    if (nombre && descripcion) {
        // Crear objeto medicamento y agregarlo a la lista
        const medicamento = { nombre, descripcion };
        medicamentos.push(medicamento);

        // Actualizar la lista visualmente
        actualizarListaMedicamentos();
        limpiarFormulario();
    }
}

// Función para limpiar el formulario
function limpiarFormulario() {
    document.getElementById('nombre').value = '';
    document.getElementById('descripcion').value = '';
}

// Función para actualizar la lista de medicamentos
function actualizarListaMedicamentos() {
    const lista = document.getElementById('listaMedicamentos');
    lista.innerHTML = ''; // Limpiar la lista

    medicamentos.forEach((medicamento, index) => {
        const listItem = document.createElement('li');
        listItem.classList.add('list-group-item');
        listItem.textContent = medicamento.nombre;
        listItem.onclick = () => mostrarEspecificaciones(index);
        lista.appendChild(listItem);
    });
}

// Función para mostrar las especificaciones del medicamento seleccionado
function mostrarEspecificaciones(index) {
    const medicamento = medicamentos[index];
    document.getElementById('nombreMedicamento').textContent = `Nombre: ${medicamento.nombre}`;
    document.getElementById('descripcionMedicamento').textContent = `Descripción: ${medicamento.descripcion}`;
}
