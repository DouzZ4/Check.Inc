$(document).ready(function () {
    if ($('#usuariosForm\\:usuario').length) {
        $('#usuariosForm\\:usuario').DataTable({
            language: {
                url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"
            },
            pageLength: 10,
            responsive: true
        });
    }

    if ($('#tablaForm\\:cita').length) {
        $('#tablaForm\\:cita').DataTable({
            language: {
                url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"
            },
            pageLength: 10,
            responsive: true
        });
    }
    
    if ($('#tablaForm\\:medicamento').length) {
        $('#tablaForm\\:medicamento').DataTable({
            language: {
                url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"
            },
            pageLength: 10,
            responsive: true
        });
    }
    
    if ($('#tablaForm\\:registro').length) {
        $('#tablaForm\\:registro').DataTable({
            language: {
                url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"
            },
            pageLength: 10,
            responsive: true
        });
    }
    
});