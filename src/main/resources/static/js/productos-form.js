document.addEventListener('DOMContentLoaded', function () {
    console.log('DOM Content Loaded!'); // Add this line
    const departamentoSelect = document.getElementById('departamentoFiltro');
    const categoriaSelect = document.getElementById('categoriaSelect');

    // Clonamos las opciones para evitar perderlas al usar innerHTML
    const allCategorias = Array.from(categoriaSelect.options).map(opt => opt.cloneNode(true));
    const defaultOption = allCategorias[0];

    departamentoSelect.addEventListener('change', function () {
        const selectedDepto = this.value;

        // Limpiar y reiniciar el select de categorías
        categoriaSelect.innerHTML = '';
        categoriaSelect.appendChild(defaultOption.cloneNode(true));

        allCategorias.forEach(option => {
            const depto = option.getAttribute('data-depto');
            if (option.value !== '' && depto === selectedDepto) {
                categoriaSelect.appendChild(option.cloneNode(true));
            }
        });
    });
});

(function () {
    'use strict';
    window.addEventListener('load', function () {
        // Obtener todos los formularios con la clase .needs-validation
        var forms = document.getElementsByClassName('needs-validation');
        // Validar cada uno al hacer submit
        Array.prototype.filter.call(forms, function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();