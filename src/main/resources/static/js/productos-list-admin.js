$(document).ready(() => {
    $("#tabla-productos").DataTable({
        // retrieve: true,
        responsive: true,
        order: [[2, "asc"]],
        lengthMenu: [10, 25, 50, 100],
        columns: [
            null,
            // {orderable: false},
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            { bSearchable: false, orderable: false }
            // {bSearchable: false},
            // {bSearchable: false},
            // {orderable: false},
            // {orderable: false},
            // {orderable: false},
        ],
        language: {
            // url: "https://cdn.datatables.net/plug-ins/1.10.24/i18n/Spanish.json",
            "search": "Buscar",
            "sLengthMenu": "Mostrar _MENU_ registros",
            "info": "Mostrando de _START_ a _END_ de _TOTAL_ productos",
            "infoFiltered": " (Filtrado de _MAX_ productos)",
            "infoEmpty": "No hay coincidencias...",
            "zeroRecords": "No hay nada aquí...",
            "emptyTable": "No hay nada aquí..."
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const deleteButtons = document.querySelectorAll(".btn-delete");
    const confirmModal = new bootstrap.Modal(document.getElementById("confirmDeleteModal"));
    const confirmBtn = document.getElementById("btnConfirmDelete");

    deleteButtons.forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault(); // Evita la redirección inmediata
            const href = this.getAttribute("data-href");
            confirmBtn.setAttribute("href", href);
            confirmModal.show();
        });
    });
});