$(document).ready(function () {
    // DataTable usuarios
    if ($('#usuariosForm\\:usuario').length) {
        let tablaUsuarios = $('#usuariosForm\\:usuario').DataTable({
            language: {url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"},
            pageLength: 10,
            responsive: true,
            columnDefs: [{orderable: false, targets: 0}]
        });

        // Select all
        $('#selectAll').on('click', function () {
            let checked = this.checked;
            $('.userCheckbox').prop('checked', checked);
        });

        $('#usuariosForm\\:usuario tbody').on('change', '.userCheckbox', function () {
            if (!this.checked)
                $('#selectAll').prop('checked', false);
        });

        // Abrir modal
        window.abrirModal = function () {
            let seleccionados = [];
            $('#usuariosForm\\:usuario tbody .userCheckbox:checked').each(function () {
                let correo = $(this).closest('tr').find('td').eq(3).text().trim();
                if (correo)
                    seleccionados.push(correo);
            });

            if (seleccionados.length === 0) {
                alert("Seleccione al menos un usuario.");
                return;
            }

            // Poner destinatarios en el textarea y en el hidden del form JSF
            $('#correoForm\\:destinatarios').val(seleccionados.join(","));
            $('#correoForm\\:destinatariosHidden').val(seleccionados.join(","));

            // Mostrar modal
            $('#correoForm\\:correoModal').css("display", "flex");
        };

        // Cerrar modal
        window.cerrarModal = function () {
            $('#correoForm\\:correoModal').css("display", "none");
        };
    }

    // Otras tablas
    if ($('#tablaForm\\:cita').length)
        $('#tablaForm\\:cita').DataTable({language: {url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"}, pageLength: 10, responsive: true});
    if ($('#tablaForm\\:medicamento').length)
        $('#tablaForm\\:medicamento').DataTable({language: {url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"}, pageLength: 10, responsive: true});
    if ($('#tablaForm\\:registro').length)
        $('#tablaForm\\:registro').DataTable({language: {url: "https://cdn.datatables.net/plug-ins/1.11.3/i18n/es_es.json"}, pageLength: 10, responsive: true});
});