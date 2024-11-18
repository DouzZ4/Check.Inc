// Función para abrir el modal
function abrirModal(tipo) {
    document.getElementById(`modal${capitalizeFirstLetter(tipo)}`).style.display = "block";
}

// Función para cerrar el modal
function cerrarModal(tipo) {
    document.getElementById(`modal${capitalizeFirstLetter(tipo)}`).style.display = "none";
}

// Función para capitalizar la primera letra de una cadena
function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

// Agregar eventos para cerrar los modales
document.querySelectorAll('.close').forEach(closeButton => {
    closeButton.addEventListener('click', function() {
        const modalId = closeButton.closest('.modal').id;
        cerrarModal(modalId.replace('modal', '').toLowerCase());
    });
});

// Función para cerrar el modal si se hace clic fuera de él
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
};
