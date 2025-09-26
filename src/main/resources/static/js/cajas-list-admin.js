$(document).ready(() => {
    $("#tabla-cajas").DataTable({
        // retrieve: true,
        responsive: true,
        order: [[2, "asc"]],
        lengthMenu: [10, 25, 50, 100],
        columns: [
            null,
            null,
            // {orderable: false},
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            { bSearchable: false, orderable: false }
            // {bSearchable: false},
        ],
        language: {
            // url: "https://cdn.datatables.net/plug-ins/1.10.24/i18n/Spanish.json",
            "search": "Buscar",
            "sLengthMenu": "Mostrar _MENU_ registros",
            "info": "Mostrando de _START_ a _END_ de _TOTAL_ cajas",
            "infoFiltered": " (Filtrado de _MAX_ cajas)",
            "infoEmpty": "No hay coincidencias...",
            "zeroRecords": "No hay nada aquí...",
            "emptyTable": "No hay nada aquí..."
        }
    });
});