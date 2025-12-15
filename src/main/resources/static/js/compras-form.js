/* compras-form.js - basado en ventas-form.js, adaptado para compras (SUMAR stock) */

let carrito = [];
const $ = id => document.getElementById(id);
const fmt = n => (Number(n || 0)).toFixed(2);

window.addEventListener('DOMContentLoaded', () => {
    if ($('fechaCompra')) {
        const now = new Date();
        $('fechaCompra').value = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
    }
    if ($('btnAgregar')) $('btnAgregar').addEventListener('click', agregarItem);
    if ($('tablaCarrito')) $('tablaCarrito').addEventListener('click', onTablaClick);
    if ($('btnCancelar')) $('btnCancelar').addEventListener('click', cancelarCompra);
    if ($('btnFinalizar')) $('btnFinalizar').addEventListener('click', finalizarCompra);
    if ($('codigo')) {
        const input = $('codigo');
        const sugerencias = $('sugerencias');
        let timeout = null;
        input.addEventListener('input', e => {
            const q = input.value.trim();
            if (timeout) clearTimeout(timeout);
            if (!q) { sugerencias.innerHTML = ''; return; }
            timeout = setTimeout(() => {
                fetch('/api/productos/buscar?q=' + encodeURIComponent(q))
                    .then(r => r.json())
                    .then(list => {
                        sugerencias.innerHTML = '';
                        list.forEach(p => {
                            const div = document.createElement('div');
                            div.className = 'autocomplete-item';
                            div.textContent = (p.codigo ? '[' + p.codigo + '] ' : '') + p.nombre + (p.precioMin ? ' — ' + p.precioMin : '');
                            div.dataset.codigo = p.codigo || '';
                            div.dataset.nombre = p.nombre || '';
                            div.dataset.precio = p.precioMin || '';
                            div.addEventListener('mousedown', () => {
                                $('codigo').value = div.dataset.codigo;
                                $('descripcion').value = div.dataset.nombre;
                                $('precio').value = div.dataset.precio;
                                sugerencias.innerHTML = '';
                                $('cantidad').focus();
                            });
                            sugerencias.appendChild(div);
                        });
                    })
                    .catch(() => { sugerencias.innerHTML = ''; });
            }, 200);
        });
        input.addEventListener('keydown', e => {
            if (e.key === 'Enter') { e.preventDefault(); agregarItem(); }
        });
    }
});

function agregarItem() {
    const codigo = $('codigo').value.trim();
    const descripcion = $('descripcion').value.trim();
    const cantidad = parseFloat($('cantidad').value || '1');
    const precio = parseFloat($('precio').value || '0');
    const iva = parseFloat($('iva').value || '21');
    const descPorc = parseFloat($('descItem').value || '0');

    if (!codigo && !descripcion) { mostrarAlerta('Código o descripción requerido', 'danger'); return; }
    if (precio <= 0 || cantidad <= 0) { mostrarAlerta('Cantidad y precio deben ser mayores a 0', 'danger'); return; }

    const idx = carrito.findIndex(i => i.codigo === codigo && i.precio === precio && i.iva === iva && i.descPorc === descPorc);
    if (idx >= 0) {
        carrito[idx].cantidad += cantidad;
    } else {
        carrito.push({ codigo, descripcion, cantidad, precio, iva, descPorc });
    }
    limpiarInputsItem();
    $('cantidad').value = 1;
    renderTabla();
    recalcular();
    $('codigo').focus();
}

function limpiarInputsItem() {
    if ($('codigo')) $('codigo').value = '';
    if ($('descripcion')) $('descripcion').value = '';
    if ($('precio')) $('precio').value = '';
    if ($('descItem')) $('descItem').value = '0';
    if ($('iva')) $('iva').value = '21';
}

function onTablaClick(e) {
    if (e.target.classList.contains('rm')) {
        const idx = parseInt(e.target.dataset.idx, 10);
        carrito.splice(idx, 1);
        renderTabla(); recalcular();
    }
}

function renderTabla() {
    const tbody = $('tablaCarrito').querySelector('tbody');
    tbody.innerHTML = '';
    carrito.forEach((it, i) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${it.codigo}</td>
      <td>${it.descripcion}</td>
      <td class="num">${it.cantidad}</td>
      <td class="num">${fmt(it.precio)}</td>
      <td class="num">${fmt(it.descPorc)}</td>
      <td class="num">${fmt(it.iva)}</td>
      <td class="num">${fmt(((it.cantidad * it.precio) - (it.cantidad * it.precio) * (it.descPorc / 100)) * (1 + it.iva / 100))}</td>
      <td><button type="button" class="rm" data-idx="${i}">Quitar</button></td>`;
        tbody.appendChild(tr);
    });
}

function recalcular() {
    let subSinIVA = 0, totalIVA = 0;
    carrito.forEach(it => {
        const base = it.cantidad * it.precio;
        const desc = base * (it.descPorc / 100);
        const neto = base - desc;
        const ivaMonto = neto * (it.iva / 100);
        subSinIVA += neto;
        totalIVA += ivaMonto;
    });
    if ($('subTotal')) $('subTotal').textContent = fmt(subSinIVA);
    if ($('montoIVA')) $('montoIVA').textContent = fmt(totalIVA);
    const totalFinal = subSinIVA + totalIVA;
    if ($('total')) { $('total').textContent = fmt(totalFinal); $('total').dataset.rawTotal = totalFinal; }
}

function cancelarCompra() {
    if (!confirm('¿Cancelar la compra actual?')) return;
    carrito = []; renderTabla(); recalcular();
    $('observaciones').value = '';
}

function buildPayload() {
    const items = carrito.map(it => ({
        codigo: it.codigo,
        descripcion: it.descripcion,
        cantidad: it.cantidad,
        precioUnitario: it.precio,
        ivaPorc: it.iva,
        descuentoPorc: it.descPorc
    }));
    return {
        fecha: $('fechaCompra') ? $('fechaCompra').value : new Date().toISOString(),
        observaciones: $('observaciones') ? $('observaciones').value : '',
        items,
        subtotalSinIVA: parseFloat(($('subTotal') && $('subTotal').textContent) || 0),
        ivaTotal: parseFloat(($('montoIVA') && $('montoIVA').textContent) || 0),
        total: parseFloat(($('total') && $('total').dataset.rawTotal) || 0)
    };
}

function finalizarCompra() {
    if (carrito.length === 0) { mostrarAlerta('No hay items en la compra', 'danger'); return; }
    if (!confirm('¿Desea finalizar la compra?')) return;

    const payload = buildPayload();
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    fetch('/compras/guardar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
        body: JSON.stringify(payload)
    })
        .then(r => {
            if (!r.ok) throw new Error('Error al guardar la compra');
            return r.json();
        })
        .then(data => {
            mostrarAlerta('Compra guardada. ID: ' + (data.id || data.numero), 'success');
            carrito = []; renderTabla(); recalcular();
            $('observaciones').value = '';
        })
        .catch(err => {
            console.error(err);
            mostrarAlerta('Error guardando la compra', 'danger');
        });
}

function mostrarAlerta(mensaje, tipo = 'success', tiempo = 4000) {
    const cont = $('alertas');
    if (!cont) return;
    const div = document.createElement('div');
    div.className = 'alert ' + (tipo === 'danger' ? 'alert-danger' : 'alert-success');
    div.textContent = mensaje;
    cont.appendChild(div);
    setTimeout(() => { div.remove(); }, tiempo);
}