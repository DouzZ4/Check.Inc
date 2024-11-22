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

// Función para abrir el modal
citasProgramadasBtn.onclick = function() {
    modalCitasProgramadas.style.display = "block";
}

// Función para cerrar el modal
closeModalCitas.onclick = function() {
    modalCitasProgramadas.style.display = "none";
}

// Cerrar el modal si el usuario hace clic fuera de la ventana modal
window.onclick = function(event) {
    if (event.target == modalCitasProgramadas) {
        modalCitasProgramadas.style.display = "none";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const btnEnviarAnomalia = document.getElementById("btnEnviarAnomalia");

    btnEnviarAnomalia.addEventListener("click", () => {
        // Captura los valores del formulario
        const tipoAnomalia = document.getElementById("tipoAnomalia").value;
        const descripcionAnomalia = document.getElementById("descripcionAnomalia").value;
        const fechaAnomalia = document.getElementById("fechaAnomalia").value;

        if (tipoAnomalia && descripcionAnomalia && fechaAnomalia) {
            // Procesa los datos (puedes enviarlos al servidor o almacenarlos)
            const datosAnomalia = {
                tipo: tipoAnomalia,
                descripcion: descripcionAnomalia,
                fecha: fechaAnomalia,
            };

            console.log("Datos de anomalía:", datosAnomalia);
            alert("Anomalía registrada exitosamente.");

            // Limpia el formulario
            document.getElementById("formAnomalias").reset();

            // Cierra el modal
            const modalElement = document.querySelector("#modalAnomalias");
            const modal = bootstrap.Modal.getInstance(modalElement);
            modal.hide();
        } else {
            alert("Por favor, completa todos los campos.");
        }
    });
});