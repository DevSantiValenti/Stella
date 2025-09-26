// === Estado ===
let carrito = []; // {codigo, descripcion, cantidad, precio, iva, descPorc}
const $ = (id) => document.getElementById(id);
const fmt = (n) => (Number(n || 0)).toFixed(2);

// === Inicialización ===
window.addEventListener('DOMContentLoaded', () => {
  const now = new Date();
  $('fechaVenta').value = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
    .toISOString().slice(0, 16);

  $('btnAgregar').addEventListener('click', agregarItem);
  $('tablaCarrito').addEventListener('click', onTablaClick);
  $('descGlobal').addEventListener('input', recalcular);
  $('montoRecibido').addEventListener('input', calcVuelto);
  $('btnCancelar').addEventListener('click', cancelarVenta);
  $('btnHold').addEventListener('click', holdVenta);
  $('btnFinalizar').addEventListener('click', finalizarVenta);
  $('ventaForm').addEventListener('submit', e => e.preventDefault());
  $('metodoPago').addEventListener('change', recalcular);

  // Enter en código agrega foco a cantidad y/o agrega directo
  // $('codigo').addEventListener('keydown', (e)=>{
  //   if(e.key==='Enter'){ $('descripcion').focus(); }
  // });
  $('codigo').focus();
});




// === Lógica de carrito ===
function agregarItem() {


  const codigo = $('codigo').value.trim();
  const descripcion = $('descripcion').value.trim();
  const cantidad = parseFloat($('cantidad').value || '1');
  const precio = parseFloat($('precio').value || '0');
  const iva = parseFloat($('iva').value || '21');
  const descPorc = parseFloat($('descItem').value || '0');

  if (!descripcion || precio <= 0 || cantidad <= 0) {
    // alert('Completá descripción, precio y cantidad válidos.');
    mostrarAlerta('Completá descripción, precio y cantidad válidos', 'warning');
    return;
  }

  // Merge por código + precio + iva + desc (para no fusionar distintos)
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
}

function limpiarInputsItem() {
  $('codigo').value = ''; $('descripcion').value = ''; $('cantidad').value = '1';
  $('precio').value = ''; $('descItem').value = '0'; $('iva').value = '21';
  $('codigo').focus();
}

function onTablaClick(e) {
  if (e.target.classList.contains('rm')) {
    const i = parseInt(e.target.dataset.i, 10);
    carrito.splice(i, 1);
    renderTabla(); recalcular();
  }
}

function renderTabla() {
  const tbody = $('tablaCarrito').querySelector('tbody');
  tbody.innerHTML = '';
  carrito.forEach((it, i) => {
    const base = it.cantidad * it.precio;
    const desc = base * (it.descPorc / 100);
    const neto = base - desc;
    const ivaMonto = neto * (it.iva / 100);
    const subtotal = neto + ivaMonto;

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${it.codigo || '-'}</td>
      <td>${it.descripcion}<div class="tiny">IVA ${fmt(it.iva)}% · Desc ${fmt(it.descPorc)}%</div></td>
      <td class="num">${fmt(it.cantidad)}</td>
      <td class="num">${fmt(it.precio)}</td>
      <td class="num">${fmt(it.descPorc)}</td>
      <td class="num">${fmt(ivaMonto)}</td>
      <td class="num">${fmt(subtotal)}</td>
      <td class="num"><button class="rm" data-i="${i}">Quitar</button></td>
    `;
    tbody.appendChild(tr);
  });
}

function recalcular() {
  let subSinIVA = 0, totalIVA = 0, totalDescItems = 0;

  carrito.forEach(it => {
    const base = it.cantidad * it.precio;
    const desc = base * (it.descPorc / 100);
    const neto = base - desc;
    const ivaMonto = neto * (it.iva / 100);

    subSinIVA += neto;
    totalIVA += ivaMonto;
    totalDescItems += desc;
  });

  const descGlobalPorc = parseFloat($('descGlobal').value || '0');
  const descGlobalMonto = (subSinIVA + totalIVA) * (descGlobalPorc / 100);

  // total antes de recargo por financiación
  const totalAntesRecargo = subSinIVA + totalIVA - descGlobalMonto;

  // recargo según método de pago
  const metodo = $('metodoPago').value;
  let recargoPorc = 0;
  if (metodo === 'CREDITO3C') recargoPorc = 0.10;
  else if (metodo === 'CREDITO6C') recargoPorc = 0.20;

  const recargoMonto = totalAntesRecargo * recargoPorc;
  const totalFinal = totalAntesRecargo + recargoMonto;

  $('subTotal').textContent = fmt(subSinIVA);
  $('montoIVA').textContent = fmt(totalIVA);
  $('montoDesc').textContent = fmt(totalDescItems + descGlobalMonto);

  // guarda recargo en data-attribute por si lo necesitás luego
  const totalEl = $('total');
  totalEl.textContent = fmt(totalFinal);
  totalEl.dataset.recargo = recargoMonto.toFixed(2);

  calcVuelto();
}

function calcVuelto() {
  const recibido = parseFloat($('montoRecibido').value || '0');
  const total = parseFloat(($('total').textContent || '0').replace(',', '.'));
  const v = Math.max(0, recibido - total);
  $('vuelto').textContent = fmt(v);
}

function cancelarVenta() {
  if (!confirm('¿Cancelar la venta actual?')) return;
  carrito = [];
  renderTabla(); recalcular();
  $('observaciones').value = ''; $('montoRecibido').value = '';
}

function holdVenta() {
  if (carrito.length === 0) { mostrarAlerta('No hay items para poner en espera', 'warning');; return; }
  const snapshot = {
    fecha: $('fechaVenta').value,
    tipoCliente: $('tipoCliente').value,
    docCliente: $('docCliente').value,
    descGlobal: parseFloat($('descGlobal').value || '0'),
    observaciones: $('observaciones').value,
    carrito
  };
  localStorage.setItem('venta_hold', JSON.stringify(snapshot));
  mostrarAlerta('Venta guardada en espera.', 'info');;
}

function finalizarVenta() {
  if (carrito.length === 0) { mostrarAlerta('Agregá al menos un ítem.', 'warning');; return; }
  // ✅ VALIDAR MONTO RECIBIDO
  const montoRecibido = parseFloat($('montoRecibido').value || '0');
  const total = parseFloat(($('total').textContent || '0').replace(',', '.'));

  if (montoRecibido < 0) {
    mostrarAlerta('Ingrese el monto recibido.', 'warning');
    $('montoRecibido').focus();
    return;
  }
  // Por si ingresa un monto menor a el total de la venta
  if (montoRecibido < total) {
    const confirmar = confirm(`El monto recibido ($${fmt(montoRecibido)}) es menor al total ($${fmt(total)}). ¿Desea continuar igual?`);
    if (!confirmar) {
      $('montoRecibido').focus();
      return;
    }
  }

  // Confirmación de envío
  if (!confirm('¿Desea finalizar la venta?')) return;
  const payload = buildPayload();
  // console.log("Enviando payload:", payload); // DEBUG

  // Obtener token CSRF
  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

  fetch('/ventas/guardar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
    body: JSON.stringify(payload)
  })
    .then(r => {
      // if(!r.ok){
      //   return r.text().then(text => {
      //     console.error("Error response text:", text);
      //     throw new Error(`Error ${r.status}: ${r.statusText} - ${text}`);
      //   });
      // }
      // return r.json();

      if (!r.ok) throw new Error('Error al guardar la venta');
      return r.json();
    })
    .then(data => {
      mostrarAlerta('Venta registrada. N°: ' + data.numero + ' - ID: ' + data.id, 'success');
      // Construye el objeto venta con los datos necesarios para el ticket
      const ventaParaTicket = {
        caja: $('caja').value,
        fecha: $('fechaVenta').value,
        numero: data.numero,
        vuelto: parseFloat(($('vuelto').textContent || '0').replace(',', '.')),
        total: parseFloat(($('total').textContent || '0').replace(',', '.')),
        recibido: parseFloat(($('montoRecibido').value || '0').replace(',', '.')),
        usuario: $('nombreCajero').value || 'Cajero',
        items: carrito.map(it => ({
          cantidad: it.cantidad,
          descripcion: it.descripcion,
          precioUnitaFrio: it.precio
        }))
      };
      // Datos del supermercado (puedes personalizarlos)
      const datosSuper = {
        nombre: "DISTRIBUIDORA STELLA",
        direccion: "SAN MARTIN 1191",
        localidad: "QUITILIPI - Chaco",
        cuit: "20341614584"
      };
      // imprimirTicket(ventaParaTicket, datosSuper); DESCOMENTAR ESTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
      // cancelarVenta();
      carrito = [];
      renderTabla(); recalcular();
      $('observaciones').value = ''; $('montoRecibido').value = '';

    })
    .catch(err => {
      console.error(err);
      mostrarAlerta('No se pudo registrar la venta.', 'danger');
      mostrarAlerta("Error: " + err.message, 'danger');
    });
  $('codigo').focus();
  // carrito = [];
  // renderTabla(); recalcular();
  // $('observaciones').value=''; $('montoRecibido').value='';
  // $('codigo').focus();
}

function buildPayload() {
  const total = parseFloat(($('total').textContent || '0').replace(',', '.'));
  const recibido = parseFloat($('montoRecibido').value || '0');
  const descGlobalPorc = parseFloat($('descGlobal').value || '0');

  // Descomponer totales
  let subSinIVA = 0, ivaTotal = 0;
  carrito.forEach(it => {
    const base = it.cantidad * it.precio;
    const desc = base * (it.descPorc / 100);
    const neto = base - desc;
    const ivaMonto = neto * (it.iva / 100);
    subSinIVA += neto;
    ivaTotal += ivaMonto;
  });
  const descGlobalMonto = (subSinIVA + ivaTotal) * (descGlobalPorc / 100);

  return {
    fecha: $('fechaVenta').value,
    tipoCliente: $('tipoCliente').value, // MINORISTA | MAYORISTA
    docCliente: $('docCliente').value || null,
    metodoPago: $('metodoPago').value,   // EFECTIVO, etc.
    descuentoGlobalPorc: descGlobalPorc,
    descuentoGlobalMonto: Number(descGlobalMonto.toFixed(2)),
    subtotalSinIVA: Number(subSinIVA.toFixed(2)),
    ivaTotal: Number(ivaTotal.toFixed(2)),
    total: Number(total.toFixed(2)),
    recibido: Number(recibido.toFixed(2)),
    vuelto: Number(Math.max(0, recibido - total).toFixed(2)),
    observaciones: $('observaciones').value || null,
    items: carrito.map(it => ({
      codigo: it.codigo || null,
      descripcion: it.descripcion,
      cantidad: Number(it.cantidad),
      precioUnitaFrio: Number(it.precio),
      ivaPorc: Number(it.iva),
      descuentoPorc: Number(it.descPorc)
    }))
  };
}

// === Autocompletado de productos ===
(function () {
  const input = $('codigo');
  const sugerencias = $('sugerencias');
  let productosEncontrados = [];
  let selectedIdx = -1;

  input.addEventListener('input', async (e) => {
    const q = e.target.value.trim();
    if (q.length < 3) {
      sugerencias.innerHTML = '';
      productosEncontrados = [];
      selectedIdx = -1;
      return;
    }
    const res = await fetch(`/api/productos/buscar?q=${encodeURIComponent(q)}`);
    productosEncontrados = await res.json();

    if (productosEncontrados.length === 0) {
      sugerencias.innerHTML = '';
      selectedIdx = -1;
      return;
    }

    // Si el código coincide exactamente y solo hay un producto, agrega automáticamente
    if (productosEncontrados.length === 1 && productosEncontrados[0].codigo === q) {
      seleccionarProducto(productosEncontrados[0]);
      return;
    }

    sugerencias.innerHTML = productosEncontrados.map((p, i) =>
      `<div class="sug${i === selectedIdx ? ' active' : ''}" data-i="${i}">${p.codigo || ''} - ${p.nombre} <span class="precio">$${fmt(p.precioMin)}</span></div>`
    ).join('');
    selectedIdx = -1;
  });

  input.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
      e.preventDefault(); // <-- Esto previene el submit SIEMPRE
      if (productosEncontrados.length === 0) return;

      if (selectedIdx >= 0) {
        seleccionarProducto(productosEncontrados[selectedIdx]);
      } else if (productosEncontrados.length === 1) {
        seleccionarProducto(productosEncontrados[0]);
      }
    }
    if (productosEncontrados.length === 0) return;

    if (e.key === 'ArrowDown') {
      selectedIdx = (selectedIdx + 1) % productosEncontrados.length;
      renderSugs();
      e.preventDefault();
    } else if (e.key === 'ArrowUp') {
      selectedIdx = (selectedIdx - 1 + productosEncontrados.length) % productosEncontrados.length;
      renderSugs();
      e.preventDefault();
    }
  });

  sugerencias.addEventListener('mousedown', e => {
    const div = e.target.closest('.sug');
    if (!div) return;
    const i = parseInt(div.dataset.i, 10);
    seleccionarProducto(productosEncontrados[i]);
  });

  function renderSugs() {
    sugerencias.innerHTML = productosEncontrados.map((p, i) =>
      `<div class="sug${i === selectedIdx ? ' active' : ''}" data-i="${i}">${p.codigo || ''} - ${p.nombre} <span class="precio">$${fmt(p.precioMin)}</span></div>`
    ).join('');
  }

  function seleccionarProducto(p) {

    // const uniSelec = parseFloat($('cantidad').value()) || 1;

    $('codigo').value = p.codigo || '';
    $('descripcion').value = p.nombre;
    $('precio').value = p.precioMin;
    // $('cantidad').value = 1;
    sugerencias.innerHTML = '';
    productosEncontrados = [];
    selectedIdx = -1;
    agregarItem(); // Agrega automáticamente
    setTimeout(() => { $('codigo').focus(); }, 100);
  }
})();
// Esto son los alert 
function mostrarAlerta(mensaje, tipo = 'success', tiempo = 4000) {
  const alertas = document.getElementById('alertas');
  const wrapper = document.createElement('div');
  wrapper.className = `alerta alerta-${tipo}`;
  wrapper.innerHTML = `
    <span>${mensaje}</span>
    <button class="cerrar" title="Cerrar" onclick="this.parentElement.remove()">×</button>
  `;
  alertas.appendChild(wrapper);

  setTimeout(() => {
    if (wrapper.parentElement) wrapper.remove();
  }, tiempo);
}

function formatearFecha(fechaIso) {
  // fechaIso puede ser "2025-08-19T18:59" o similar
  const d = new Date(fechaIso);
  const pad = n => n.toString().padStart(2, '0');
  return `${pad(d.getDate())}-${pad(d.getMonth() + 1)}-${pad(d.getFullYear())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

// Para que funcione los reportes
function imprimirTicket(venta, supermercado) {
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: [80, 120 + venta.items.length * 8]
  });

  let y = 8;
  doc.setFont('courier', 'normal');
  doc.setFontSize(10);

  // Comercio
  doc.text(supermercado.nombre, 5, y);
  y += 5;
  doc.setFontSize(8);
  doc.text(supermercado.direccion, 5, y);
  y += 4;
  doc.text(supermercado.localidad, 5, y);
  y += 4;
  doc.text('Cuit: ' + supermercado.cuit, 5, y);
  y += 4;
  doc.text('----------------------------------------', 5, y);
  y += 4;

  // Fecha y venta
  doc.text('Fecha: ' + formatearFecha(venta.fecha), 5, y);
  y += 4;
  doc.text('Venta N°: ' + venta.numero, 5, y);
  y += 4;
  doc.text(venta.caja + ' : ' + (venta.usuario || 'Cajero'), 5, y);
  y += 4;
  doc.text('----------------------------------------', 5, y);
  y += 4;

  // Encabezado de tabla
  doc.setFontSize(8);
  doc.text('Can  Producto', 5, y);
  doc.text('$Unit', 48, y);
  doc.text('$Subt', 75, y, { align: 'right' });
  y += 3;
  doc.text('----------------------------------------', 5, y);
  y += 4;

  // Detalle de productos
  venta.items.forEach(item => {
    let nombre = item.descripcion.length > 12 ? item.descripcion.substring(0, 12) : item.descripcion;
    doc.text(String(item.cantidad), 5, y);
    doc.text(nombre, 13, y);
    doc.text(fmt(item.precioUnitaFrio), 60, y, { align: 'right' });
    doc.text(fmt(item.cantidad * item.precioUnitaFrio), 75, y, { align: 'right' });
    y += 4;
  });

  doc.text('----------------------------------------', 5, y);
  y += 4;

  // Totales
  doc.setFontSize(9);
  doc.text('TOTAL:'.padEnd(18) + '$' + fmt(venta.total), 5, y);
  y += 5;
  doc.text('PAGO:'.padEnd(19) + '$' + fmt(venta.recibido), 5, y);
  y += 5;
  doc.text('SU VUELTO:'.padEnd(16) + '$' + fmt(venta.vuelto), 5, y);
  y += 5;
  doc.text('----------------------------------------', 5, y);
  y += 5;

  // Mensaje final
  doc.setFontSize(8);
  doc.text('¡Gracias por su compra!', 5, y);

  doc.save('ticket.pdf');
}



