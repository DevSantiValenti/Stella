/* ventas-form.js - versión limpia y consolidada */
let carrito = []; // {codigo, descripcion, cantidad, precio, iva, descPorc}
const $ = (id) => document.getElementById(id);
const fmt = (n) => (Number(n || 0)).toFixed(2);

// util: métodos de pago
const METODOS = ['EFECTIVO', 'DEBITO', 'CREDITO1C', 'CREDITO3C', 'CREDITO6C', 'TRANSFERENCIA', 'CTA_CORRIENTE'];

/* ========== Inicialización ========== */
window.addEventListener('DOMContentLoaded', () => {
  // fecha
  const now = new Date();
  if ($('fechaVenta')) $('fechaVenta').value = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);

  // listeners principales (comprueba existencia)
  if ($('btnAgregar')) $('btnAgregar').addEventListener('click', agregarItem);
  if ($('tablaCarrito')) $('tablaCarrito').addEventListener('click', onTablaClick);
  if ($('descGlobal')) $('descGlobal').addEventListener('input', recalcular);
  if ($('btnCancelar')) $('btnCancelar').addEventListener('click', cancelarVenta);
  if ($('btnHold')) $('btnHold').addEventListener('click', holdVenta);
  if ($('btnFinalizar')) {
    $('btnFinalizar').addEventListener('click', finalizarVenta);
    $('btnFinalizar').disabled = true; // inicio deshabilitado hasta cubrir pagos
  }
  if ($('ventaForm')) $('ventaForm').addEventListener('submit', e => e.preventDefault());
  // if ($('metodoPago')) $('metodoPago').addEventListener('change', recalcular);

  // payments UI: delegación sólida
  const paymentsContainer = document.getElementById('paymentsContainer');
  if (document.getElementById('btnAddPayment')) document.getElementById('btnAddPayment').addEventListener('click', () => addPaymentRow());

  if (paymentsContainer) {
    // delegado: captura cambios/inputs en selects e inputs generados dinámicamente
    paymentsContainer.addEventListener('input', (ev) => {
      const t = ev.target;
      if (t && (t.matches('.pay-amount') || t.matches('.pay-method'))) {
        onPaymentsChange();
      }
    });

    // Aseguro también el 'change' (para selects) y que recalcule siempre
    paymentsContainer.addEventListener('change', (ev) => {
      const t = ev.target;
      if (t && (t.matches('.pay-method') || t.matches('.pay-amount'))) {
        onPaymentsChange();
        recalcular();
      }
    });

    // crea una fila por defecto *después* de instalar la delegación y recalcula
    addPaymentRow('EFECTIVO', 0);
    recalcular();
  }

  if ($('codigo')) $('codigo').focus();
});

/* ========== Carrito ========== */
function agregarItem() {
  const codigo = $('codigo').value.trim();
  const descripcion = $('descripcion').value.trim();
  const cantidad = parseFloat($('cantidad').value || '1');
  const precio = parseFloat($('precio').value || '0');
  const iva = parseFloat($('iva').value || '21');
  const descPorc = parseFloat($('descItem').value || '0');

  if (!descripcion || precio <= 0 || cantidad <= 0) { mostrarAlerta('Completá descripción, precio y cantidad válidos', 'warning'); return; }

  const idx = carrito.findIndex(i => i.codigo === codigo && i.precio === precio && i.iva === iva && i.descPorc === descPorc);
  if (idx >= 0) carrito[idx].cantidad += cantidad; else carrito.push({ codigo, descripcion, cantidad, precio, iva, descPorc });

  limpiarInputsItem(); $('cantidad').value = 1; renderTabla(); recalcular();
}
function limpiarInputsItem() { if ($('codigo')) $('codigo').value = ''; if ($('descripcion')) $('descripcion').value = ''; if ($('precio')) $('precio').value = ''; if ($('descItem')) $('descItem').value = '0'; if ($('iva')) $('iva').value = '21'; if ($('codigo')) $('codigo').focus(); }
function onTablaClick(e) { if (e.target.classList.contains('rm')) { const i = parseInt(e.target.dataset.i, 10); carrito.splice(i, 1); renderTabla(); recalcular(); } }
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

// REEMPLAZAR cualquier otra definición de recalcular por esta (única versión)
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

  const descGlobalPorc = parseFloat(($('descGlobal') && $('descGlobal').value) || '0');
  const descGlobalMonto = (subSinIVA + totalIVA) * (descGlobalPorc / 100);
  const totalAntesRecargo = subSinIVA + totalIVA - descGlobalMonto;

  // --- Recargo según método de pago ---
  let recargoPorc = 0;
  const pagos = (typeof getPaymentsFromUI === 'function') ? getPaymentsFromUI() : [];
  if (pagos && pagos.length > 0) {
    for (const p of pagos) {
      if (!p || !p.metodoPago) continue;
      if (p.metodoPago === 'CREDITO3C') recargoPorc = Math.max(recargoPorc, 0.10);
      else if (p.metodoPago === 'CREDITO6C') recargoPorc = Math.max(recargoPorc, 0.20);
    }
  } else {
    // fallback si solo se usa el selector global
    const metodoGlobal = $('metodoPago') ? $('metodoPago').value : null;
    if (metodoGlobal === 'CREDITO3C') recargoPorc = 0.10;
    else if (metodoGlobal === 'CREDITO6C') recargoPorc = 0.20;
  }

  const recargoMonto = totalAntesRecargo * recargoPorc;
  const totalFinal = totalAntesRecargo + recargoMonto;

  if ($('subTotal')) $('subTotal').textContent = fmt(subSinIVA);
  if ($('montoIVA')) $('montoIVA').textContent = fmt(totalIVA);
  if ($('montoDesc')) $('montoDesc').textContent = fmt(totalDescItems + descGlobalMonto);

  const totalEl = $('total');
  if (totalEl) {
    totalEl.textContent = fmt(totalFinal);
    totalEl.dataset.rawTotal = totalFinal.toFixed(2);
    totalEl.dataset.recargo = recargoMonto.toFixed(2);
  }

  // recalcula vuelto / habilitación de Finalizar
  onPaymentsChange();
}

function calcVuelto() {
  const payments = getPaymentsFromUI();
  const totalRaw = (document.getElementById('total') && document.getElementById('total').dataset.rawTotal) || (document.getElementById('total') && document.getElementById('total').textContent) || '0';
  const total = parseFloat(String(totalRaw).replace(',', '.')) || 0;

  if (payments && payments.length > 0) {
    const sumaPagos = payments.reduce((s, p) => s + (p.monto || 0), 0);
    document.getElementById('vuelto').textContent = fmt(Math.max(0, Number((sumaPagos - total).toFixed(2))));
    const btnFinal = document.getElementById('btnFinalizar');
    if (btnFinal) btnFinal.disabled = Number((sumaPagos - total).toFixed(2)) < 0;
    return;
  }

  const recibido = parseFloat(($('montoRecibido') && $('montoRecibido').value) || '0');
  const v = Math.max(0, recibido - total);
  if ($('vuelto')) $('vuelto').textContent = fmt(v);
  const btnFinal = document.getElementById('btnFinalizar');
  if (btnFinal) btnFinal.disabled = recibido < total;
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

// --------------------------------------------------
// helper: asigna pagos hasta cubrir 'total'
// --------------------------------------------------
function allocatePayments(pagos, total) {
  const allocated = [];
  let remaining = Number(total || 0);
  for (const p of (pagos || [])) {
    if (remaining <= 0) break;
    const available = Number(p.monto || 0);
    const take = Math.min(available, remaining);
    allocated.push({
      metodoPago: p.metodoPago,
      monto: Number(take.toFixed(2))
    });
    remaining = Number((remaining - take).toFixed(2));
  }
  return allocated;
}

// --------------------------------------------------
// onPaymentsChange: habilita Finalizar si la suma de pagos cubre el total
// y calcula vuelto (suma original - total)
// --------------------------------------------------
function onPaymentsChange() {
  const pagos = getPaymentsFromUI();
  const sumaPagos = pagos.reduce((s, p) => s + (p.monto || 0), 0);
  const totalRaw = (document.getElementById('total') && document.getElementById('total').dataset.rawTotal) || (document.getElementById('total') && document.getElementById('total').textContent) || '0';
  const total = parseFloat(String(totalRaw).replace(',', '.')) || 0;

  // vuelto: dinero real entregado menos lo que efectivamente se utilizará (hasta total)
  const allocated = allocatePayments(pagos, total);
  const allocatedSum = allocated.reduce((s, p) => s + (p.monto || 0), 0);

  const vuelto = Number((sumaPagos - allocatedSum).toFixed(2)); // sobrante real
  const vueltoEl = document.getElementById('vuelto');
  if (vueltoEl) vueltoEl.textContent = fmt(Math.max(0, vuelto));

  const btnFinal = document.getElementById('btnFinalizar');
  // habilitar solo si la suma original cubre el total (o si la asignación logró cubrir el total)
  if (btnFinal) btnFinal.disabled = Number((sumaPagos - total).toFixed(2)) < 0;
}

// --------------------------------------------------
// buildPayload: envía pagos "asignados" (no el exceso)
// --------------------------------------------------
function buildPayload() {
  const total = parseFloat(($('total') && $('total').dataset.rawTotal) || ($('total') && $('total').textContent) || '0') || 0;
  const descGlobalPorc = parseFloat(($('descGlobal') && $('descGlobal').value) || '0');

  // legacy: monto recibido por input (si todavía existe en la plantilla)
  const recibidoInput = parseFloat(($('montoRecibido') && $('montoRecibido').value) || '0');

  let subSinIVA = 0, ivaTotal = 0;
  carrito.forEach(it => {
    const base = it.cantidad * it.precio;
    const desc = base * (it.descPorc / 100);
    const neto = base - desc;
    const ivaMonto = neto * (it.iva / 100);
    subSinIVA += neto; ivaTotal += ivaMonto;
  });
  const descGlobalMonto = (subSinIVA + ivaTotal) * (descGlobalPorc / 100);

  // pagos desde UI (si existen filas de pago)
  const pagos = (typeof getPaymentsFromUI === 'function') ? getPaymentsFromUI() : [];

  // Si hay pagos personalizados, calcular recibido/vuelto desde ellos, si no usar input legacy
  let recibido = recibidoInput || 0;
  if (pagos && pagos.length > 0) {
    recibido = pagos.reduce((s, p) => s + (p.monto || 0), 0);
  }

  // Asignación de pagos hasta cubrir total (mantiene comportamiento previo)
  const pagosAsignados = allocatePayments(pagos, total);
  const recibidoEnviado = pagosAsignados.reduce((s, p) => s + (p.monto || 0), 0);
  const vueltoReal = Number(Math.max(0, ( (pagos.length>0 ? (pagos.reduce((s,p)=>s+(p.monto||0),0)) : recibido) - recibidoEnviado )).toFixed(2));

  return {
    fecha: $('fechaVenta') ? $('fechaVenta').value : new Date().toISOString(),
    tipoCliente: $('tipoCliente') ? $('tipoCliente').value : 'MINORISTA',
    docCliente: $('docCliente') ? $('docCliente').value || null : null,
    metodoPago: $('metodoPago') ? $('metodoPago').value : 'EFECTIVO',
    descuentoGlobalPorc: descGlobalPorc,
    descuentoGlobalMonto: Number(descGlobalMonto.toFixed(2)),
    subtotalSinIVA: Number(subSinIVA.toFixed(2)),
    ivaTotal: Number(ivaTotal.toFixed(2)),
    total: Number(total.toFixed(2)),
    recibido: Number(recibidoEnviado.toFixed(2)),    // enviamos sólo lo asignado (<= total)
    vuelto: Number(vueltoReal.toFixed(2)),           // y el resto como vuelto
    observaciones: $('observaciones') ? $('observaciones').value || null : null,
    pagos: pagosAsignados.length ? pagosAsignados : undefined,
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

// crea una fila de pago en el DOM
function addPaymentRow(method = 'EFECTIVO', amount = 0) {
  const c = document.getElementById('paymentsContainer');
  if (!c) return;
  const idx = c.children.length;
  const row = document.createElement('div');
  row.className = 'payment-row';
  row.dataset.idx = idx;
  row.innerHTML = `
    <select class="pay-method">${METODOS.map(m => `<option value="${m}" ${m === method ? 'selected' : ''}>${m}</option>`).join('')}</select>
    <input type="number" class="pay-amount" min="0" step="0.01" value="${Number(amount).toFixed(2)}" />
    <button type="button" class="pay-remove">Eliminar</button>
  `;
  c.appendChild(row);

  // listeners por fila (útiles para accesibilidad) - la delegación también los cubre
  const sel = row.querySelector('.pay-method');
  const amt = row.querySelector('.pay-amount');
  sel.addEventListener('change', () => { onPaymentsChange(); recalcular(); });
  amt.addEventListener('input', onPaymentsChange);
  row.querySelector('.pay-remove').addEventListener('click', () => { row.remove(); onPaymentsChange(); recalcular(); });

  // FORZAR disparo para que el select recién creado active la lógica
  sel.dispatchEvent(new Event('change', { bubbles: true }));

  onPaymentsChange();
}
function getPaymentsFromUI() {
  const rows = Array.from(document.querySelectorAll('#paymentsContainer .payment-row'));
  return rows.map(r => {
    const metodo = r.querySelector('.pay-method').value;
    const monto = parseFloat(r.querySelector('.pay-amount').value || '0');
    return { metodoPago: metodo, monto: Number(monto.toFixed(2)) };
  });
}
function onPaymentsChange() {
  const pagos = getPaymentsFromUI();
  const sumaPagos = pagos.reduce((s, p) => s + (p.monto || 0), 0);
  const totalRaw = (document.getElementById('total') && document.getElementById('total').dataset.rawTotal) || (document.getElementById('total') && document.getElementById('total').textContent) || '0';
  const total = parseFloat(String(totalRaw).replace(',', '.')) || 0;
  const diff = Number((sumaPagos - total).toFixed(2));
  const vueltoEl = document.getElementById('vuelto'); if (vueltoEl) vueltoEl.textContent = fmt(Math.max(0, diff));
  const btnFinal = document.getElementById('btnFinalizar');
  if (btnFinal) btnFinal.disabled = diff < 0;
}


/* ========== Finalizar venta ========== */
async function finalizarVenta() {
  if (carrito.length === 0) { mostrarAlerta('Agregá al menos un ítem.', 'warning'); return; }

  const pagos = getPaymentsFromUI();
  if (!pagos || pagos.length === 0) { mostrarAlerta('Agregá al menos una forma de pago.', 'warning'); return; }

  const total = parseFloat((($('total') && $('total').dataset.rawTotal) || ($('total') && $('total').textContent) || '0').toString().replace(',', '.')) || 0;
  const recibido = pagos.reduce((s, p) => s + (p.monto || 0), 0);

  if (Number((recibido - total).toFixed(2)) < 0) {
    if (!confirm(`La suma de pagos ($${fmt(recibido)}) es menor al total ($${fmt(total)}). ¿Desea continuar?`)) return;
  }

  if (!confirm('¿Desea finalizar la venta?')) return;

  const payload = buildPayload();
  // CSRF
  const csrfToken = document.querySelector('meta[name="_csrf"]') ? document.querySelector('meta[name="_csrf"]').content : null;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]') ? document.querySelector('meta[name="_csrf_header"]').content : 'X-CSRF-TOKEN';

  try {
    const r = await fetch('/ventas/guardar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...(csrfToken ? { [csrfHeader]: csrfToken } : {}) },
      body: JSON.stringify(payload)
    });
    if (!r.ok) throw new Error('Error al guardar la venta');
    const data = await r.json();
    mostrarAlerta('Venta registrada. N°: ' + data.numero + ' - ID: ' + data.id, 'success');
    // limpiar UI
    carrito = []; renderTabla(); recalcular();
    if ($('observaciones')) $('observaciones').value = '';
    const pc = document.getElementById('paymentsContainer'); if (pc) pc.innerHTML = ''; addPaymentRow('EFECTIVO', 0);
  } catch (err) {
    console.error(err);
    mostrarAlerta('No se pudo registrar la venta.', 'danger');
  } finally { if ($('codigo')) $('codigo').focus(); }
}

/* ========== Payload ========== */
function buildPayload() {
  const total = parseFloat(($('total') && $('total').dataset.rawTotal) || ($('total') && $('total').textContent) || '0') || 0;
  const descGlobalPorc = parseFloat(($('descGlobal') && $('descGlobal').value) || '0');

  // legacy: monto recibido por input (si todavía existe en la plantilla)
  const recibidoInput = parseFloat(($('montoRecibido') && $('montoRecibido').value) || '0');

  let subSinIVA = 0, ivaTotal = 0;
  carrito.forEach(it => {
    const base = it.cantidad * it.precio;
    const desc = base * (it.descPorc / 100);
    const neto = base - desc;
    const ivaMonto = neto * (it.iva / 100);
    subSinIVA += neto; ivaTotal += ivaMonto;
  });
  const descGlobalMonto = (subSinIVA + ivaTotal) * (descGlobalPorc / 100);

  // pagos desde UI (si existen filas de pago)
  const pagos = (typeof getPaymentsFromUI === 'function') ? getPaymentsFromUI() : [];

  // Si hay pagos personalizados, calcular recibido/vuelto desde ellos, si no usar input legacy
  let recibido = recibidoInput || 0;
  if (pagos && pagos.length > 0) {
    recibido = pagos.reduce((s, p) => s + (p.monto || 0), 0);
  }

  // Asignación de pagos hasta cubrir total (mantiene comportamiento previo)
  const pagosAsignados = allocatePayments(pagos, total);
  const recibidoEnviado = pagosAsignados.reduce((s, p) => s + (p.monto || 0), 0);
  const vueltoReal = Number(Math.max(0, ( (pagos.length>0 ? (pagos.reduce((s,p)=>s+(p.monto||0),0)) : recibido) - recibidoEnviado )).toFixed(2));

  return {
    fecha: $('fechaVenta') ? $('fechaVenta').value : new Date().toISOString(),
    tipoCliente: $('tipoCliente') ? $('tipoCliente').value : 'MINORISTA',
    docCliente: $('docCliente') ? $('docCliente').value || null : null,
    metodoPago: $('metodoPago') ? $('metodoPago').value : 'EFECTIVO',
    descuentoGlobalPorc: descGlobalPorc,
    descuentoGlobalMonto: Number(descGlobalMonto.toFixed(2)),
    subtotalSinIVA: Number(subSinIVA.toFixed(2)),
    ivaTotal: Number(ivaTotal.toFixed(2)),
    total: Number(total.toFixed(2)),
    recibido: Number(recibidoEnviado.toFixed(2)),    // enviamos sólo lo asignado (<= total)
    vuelto: Number(vueltoReal.toFixed(2)),           // y el resto como vuelto
    observaciones: $('observaciones') ? $('observaciones').value || null : null,
    pagos: pagosAsignados.length ? pagosAsignados : undefined,
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
  if (!alertas) return;
  const wrapper = document.createElement('div'); wrapper.className = `alerta alerta-${tipo}`;
  wrapper.innerHTML = `<span>${mensaje}</span><button class="cerrar" title="Cerrar" onclick="this.parentElement.remove()">×</button>`;
  alertas.appendChild(wrapper);
  setTimeout(() => { if (wrapper.parentElement) wrapper.remove(); }, tiempo);
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