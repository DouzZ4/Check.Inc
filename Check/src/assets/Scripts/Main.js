document.addEventListener("DOMContentLoaded", () => {
    // Inicializar la barra de navegación
    fetch("../assets/Components/navbar.html")
        .then(response => response.text())
        .then(data => {
            document.getElementById("navbar-container").innerHTML = data;
        })
        .catch(error => console.error("Error cargando la barra de navegación:", error));

    fetch("../assets/Components/NavbarInicio.html")
        .then(response => response.text())
        .then(data => {
            document.getElementById("NavbarInicio-container").innerHTML = data;

            // Ahora que la barra de navegación se ha cargado, asignar eventos
            const menuToggle = document.getElementById("menuToggle");
            const navLinks = document.getElementById("navLinks");

            if (menuToggle && navLinks) {
                menuToggle.addEventListener("click", () => {
                    navLinks.classList.toggle("show");
                });
            }
        })
        .catch(error => console.error("Error cargando la barra de navegación:", error));

    // Obtener elementos del DOM después de que el contenido se haya cargado
    const modalCitasProgramadas = document.getElementById("modalCitasProgramadas");
    const citasProgramadasBtn = document.getElementById("citasProgramadasBtn");
    const closeModalCitas = document.querySelector(".modal .close");

    // Abrir el modal de citas programadas
    if (citasProgramadasBtn && modalCitasProgramadas) {
        citasProgramadasBtn.onclick = function() {
            modalCitasProgramadas.style.display = "block";
        };
    }

    // Cerrar el modal de citas programadas
    if (closeModalCitas && modalCitasProgramadas) {
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
});
