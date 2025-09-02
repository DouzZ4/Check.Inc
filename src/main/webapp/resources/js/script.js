$(document).ready(function() {
    $('#usuariosForm\\:usuario').DataTable({
        language: {
            url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"
        },
        pageLength: 10,
        responsive: true
    });
});
