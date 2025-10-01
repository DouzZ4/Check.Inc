$(document).ready(function () {
    const APP_TOKEN = "";
    const API_URL = "https://www.datos.gov.co/resource/i7cb-raxc.json";
    let dataTable = null;

    // --- Función para pasar texto a formato de oración ---
    function toSentenceCase(str) {
        if (!str) return "";
        str = str.toLowerCase();
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    function mostrarMensaje(mensaje, tipo) {
        $("#mensajesInvima")
            .removeClass()
            .addClass("message-box " + tipo)
            .html(mensaje)
            .show();
    }

    function limpiarResultados() {
        if (dataTable) {
            dataTable.clear().destroy();
            dataTable = null;
        }
        $("#tablaMedicamentos tbody").empty();
        $("#tablaMedicamentos").hide();
        $("#mensajesInvima").hide();
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
            success: function (data) {
                if (!data || data.length === 0) {
                    mostrarMensaje("No se encontraron medicamentos con ese nombre", "info-message");
                    return;
                }

                const rows = data.map(function (med) {
                    const expediente = med.expediente || '';
                    const producto = toSentenceCase(med.producto);
                    const titular = toSentenceCase(med.titular);

                    const medFormatted = {
                        ...med,
                        producto: producto,
                        titular: titular
                    };

                    const medEncoded = encodeURIComponent(JSON.stringify(medFormatted));

                    return [
                        expediente,
                        producto,
                        titular,
                        `<button type="button" 
                                 class="btn btn-primary submit-button btn-select"
                                 data-med="${medEncoded}">
                            Seleccionar
                         </button>`
                    ];
                });

                // Mostrar tabla y cargar DataTable
                $("#tablaMedicamentos").show();

                dataTable = $("#tablaMedicamentos").DataTable({
                    data: rows,
                    columns: [
                        { title: "Expediente" },
                        { title: "Producto" },
                        { title: "Titular" },
                        { title: "Seleccionar", orderable: false, searchable: false }
                    ],
                    pageLength: 10,
                    lengthChange: false,
                    language: {
                        url: "https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
                    }
                });

                $("#mensajesInvima").hide();
            },
            error: function (xhr, status, error) {
                mostrarMensaje(
                    "Error al consultar el servicio. Por favor, intente más tarde.",
                    "error-message"
                );
                console.error("Error en la consulta:", error);
            }
        });
    }

    // Manejador delegado para botones "Seleccionar"
    $(document).on("click", ".btn-select", function () {
        try {
            const medEncoded = $(this).attr("data-med");
            if (!medEncoded) return;
            const med = JSON.parse(decodeURIComponent(medEncoded));

            // Buscar el campo nombre del medicamento
            let $input = $(".medicamento-nombre");
            if ($input.length === 0) {
                $input = $('[id$=":nombre"]');
            }
            if ($input.length === 0) {
                $input = $('[id$="nombre"]');
            }

            if ($input.length) {
                $input.val(med.producto || "").trigger("input").trigger("change");
            }

            // Scroll al formulario
            $("html, body").animate({
                scrollTop: $("#formMedicamento").offset().top
            }, 500);
        } catch (err) {
            console.error("Error al seleccionar medicamento:", err);
        }
    });

    // Event listeners de búsqueda
    $("#btnBuscarMedicamento").click(buscarMedicamentos);

    $("#searchMedicamento").on("keypress", function (e) {
        if (e.which === 13) { // Enter
            buscarMedicamentos();
        }
    });

    // Búsqueda automática con debounce
    let timeout = null;
    $("#searchMedicamento").on("input", function () {
        clearTimeout(timeout);
        timeout = setTimeout(buscarMedicamentos, 500);
    });
});
