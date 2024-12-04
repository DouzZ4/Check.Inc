// main.js
document.addEventListener("DOMContentLoaded", () => {
    const menuToggle = document.getElementById("menuToggle");
    const navLinks = document.getElementById("navLinks");

    if (menuToggle && navLinks) {
        menuToggle.addEventListener("click", () => {
            navLinks.classList.toggle("show");
        });
    }
});

// Abrir el modal de citas programadas
function abrirModalCitasProgramadas() {
    document.getElementById("modalCitasProgramadas").style.display = "block";
}

// Cerrar el modal de citas programadas
function cerrarModalCitasProgramadas() {
    document.getElementById("modalCitasProgramadas").style.display = "none";
}

// Obtener elementos del DOM
const modalCitasProgramadas = document.getElementById("modalCitasProgramadas");
const citasProgramadasBtn = document.getElementById("citasProgramadasBtn");
const closeModalCitas = document.querySelector(".modal .close");

// Verifica si los elementos existen antes de asignar eventos
if (citasProgramadasBtn) {
    citasProgramadasBtn.onclick = function() {
        modalCitasProgramadas.style.display = "block";
    };
}

if (closeModalCitas) {
    closeModalCitas.onclick = function() {
        modalCitasProgramadas.style.display = "none";
    };
}

// Cerrar el modal si el usuario hace clic fuera de la ventana modal
window.onclick = function(event) {
    if (event.target === modalCitasProgramadas) {
        modalCitasProgramadas.style.display = "none";
    }
};

document.addEventListener("DOMContentLoaded", function() { fetch("../assets/Components/navbar.html") .then(response => response.text()) .then(data => { document.getElementById("navbar-container").innerHTML = data; }) .catch(error => console.error("Error cargando la barra de navegación:", error)); });