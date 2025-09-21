$(document).ready(function() {
    const APP_TOKEN = "eYzoy7NJVkqnCYmCUNWMHyNj5";
    const API_URL = "https://www.datos.gov.co/resource/i7cb-raxc.json";
    
    function mostrarMensaje(mensaje, tipo) {
        $("#mensajesInvima")
            .removeClass()
            .addClass("message-box " + tipo)
            .html(mensaje)
            .show();
    }
    
    function limpiarResultados() {
        $("#tablaMedicamentos tbody").empty();
        $("#tablaMedicamentos").hide();
        $("#mensajesInvima").hide();
    }

    window.seleccionarMedicamento = function(med) {
        $("#nombre").val(med.producto || '');
        // Desplazar hasta el formulario de registro
        $('html, body').animate({
            scrollTop: $("#formMedicamento").offset().top
        }, 500);
    }
    
    function buscarMedicamentos() {
        const terminoBusqueda = $("#searchMedicamento").val().trim();
        
        if (terminoBusqueda.length < 3) {
            mostrarMensaje("Por favor, ingrese al menos 3 caracteres para buscar", "error-message");
            return;
        }
        
        limpiarResultados();
        mostrarMensaje("Buscando medicamentos...", "info-message");
        
        $.ajax({
            url: API_URL,
            type: "GET",
            data: {
                "$where": `lower(producto) like '%${terminoBusqueda.toLowerCase()}%'`,
                "$limit": 50,
                "$$app_token": APP_TOKEN
            },
            success: function(data) {
                if (!data || data.length === 0) {
                    mostrarMensaje("No se encontraron medicamentos con ese nombre", "info-message");
                    return;
                }
                
                const tbody = $("#tablaMedicamentos tbody");
                data.forEach(function(med) {
                    tbody.append(`
                        <tr>
                            <td>${med.expediente || ''}</td>
                            <td>${med.producto || ''}</td>
                            <td>${med.titular || ''}</td>
                            <td>${med.registro_sanitario || ''}</td>
                            <td>${med.principio_activo || ''}</td>
                            <td>${med.concentracion || ''}</td>
                            <td>
                                <button type="button" class="action-button"
                                        onclick='seleccionarMedicamento(${JSON.stringify(med)})'>
                                    Seleccionar
                                </button>
                            </td>
                        </tr>
                    `);
                });
                
                $("#tablaMedicamentos").show();
                $("#mensajesInvima").hide();
            },
            error: function(xhr, status, error) {
                mostrarMensaje(
                    "Error al consultar el servicio. Por favor, intente más tarde.", 
                    "error-message"
                );
                console.error("Error en la consulta:", error);
            }
        });
    }
    
    // Event listeners
    $("#btnBuscarMedicamento").click(buscarMedicamentos);
    
    $("#searchMedicamento").on('keypress', function(e) {
        if (e.which === 13) { // Enter key
            buscarMedicamentos();
        }
    });

    // Debounce para búsqueda automática
    let timeout = null;
    $("#searchMedicamento").on('input', function() {
        clearTimeout(timeout);
        timeout = setTimeout(buscarMedicamentos, 500);
    });
});