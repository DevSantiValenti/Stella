// === Estado ===
let carrito = []; // {codigo, descripcion, cantidad, precio, iva, descPorc}
const $ = (id) => document.getElementById(id);
const fmt = (n) => (Number(n||0)).toFixed(2);

// === Inicialización ===
window.addEventListener('DOMContentLoaded', () => {
  const now = new Date();
  $('fechaVenta').value = new Date(now.getTime() - now.getTimezoneOffset()*60000)
    .toISOString().slice(0,16);

  $('btnAgregar').addEventListener('click', agregarItem);
  $('tablaCarrito').addEventListener('click', onTablaClick);
  $('descGlobal').addEventListener('input', recalcular);
  $('montoRecibido').addEventListener('input', calcVuelto);
  $('btnCancelar').addEventListener('click', cancelarVenta);
  $('btnHold').addEventListener('click', holdVenta);
  $('btnFinalizar').addEventListener('click', finalizarVenta);
  $('ventaForm').addEventListener('submit', e => e.preventDefault());

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

  if(!descripcion || precio<=0 || cantidad<=0){
    // alert('Completá descripción, precio y cantidad válidos.');
    mostrarAlerta('Completá descripción, precio y cantidad válidos', 'warning');
    return;
  }

  // Merge por código + precio + iva + desc (para no fusionar distintos)
  const idx = carrito.findIndex(i => i.codigo===codigo && i.precio===precio && i.iva===iva && i.descPorc===descPorc);
  if(idx>=0){
    carrito[idx].cantidad += cantidad;
  }else{
    carrito.push({codigo, descripcion, cantidad, precio, iva, descPorc});
  }

  limpiarInputsItem();
  renderTabla();
  recalcular();
}

function limpiarInputsItem(){
  $('codigo').value=''; $('descripcion').value=''; $('cantidad').value='1';
  $('precio').value=''; $('descItem').value='0'; $('iva').value='21';
  $('codigo').focus();
}

function onTablaClick(e){
  if(e.target.classList.contains('rm')){
    const i = parseInt(e.target.dataset.i,10);
    carrito.splice(i,1);
    renderTabla(); recalcular();
  }
}

function renderTabla(){
  const tbody = $('tablaCarrito').querySelector('tbody');
  tbody.innerHTML = '';
  carrito.forEach((it, i)=>{
    const base = it.cantidad * it.precio;
    const desc = base * (it.descPorc/100);
    const neto = base - desc;
    const ivaMonto = neto * (it.iva/100);
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

function recalcular(){
  let subSinIVA = 0, totalIVA = 0, total = 0, totalDescItems = 0;

  carrito.forEach(it=>{
    const base = it.cantidad * it.precio;
    const desc = base * (it.descPorc/100);
    const neto = base - desc;
    const ivaMonto = neto * (it.iva/100);

    subSinIVA += neto;
    totalIVA += ivaMonto;
    totalDescItems += desc;
  });

  const descGlobalPorc = parseFloat($('descGlobal').value||'0');
  const descGlobalMonto = (subSinIVA + totalIVA) * (descGlobalPorc/100);
  const totalFinal = subSinIVA + totalIVA - descGlobalMonto;

  $('subTotal').textContent = fmt(subSinIVA);
  $('montoIVA').textContent = fmt(totalIVA);
  $('montoDesc').textContent = fmt(totalDescItems + descGlobalMonto);
  $('total').textContent = fmt(totalFinal);

  calcVuelto();
}

function calcVuelto(){
  const recibido = parseFloat($('montoRecibido').value||'0');
  const total = parseFloat(($('total').textContent||'0').replace(',','.'));
  const v = Math.max(0, recibido - total);
  $('vuelto').textContent = fmt(v);
}

function cancelarVenta(){
  if(!confirm('¿Cancelar la venta actual?')) return;
  carrito = [];
  renderTabla(); recalcular();
  $('observaciones').value=''; $('montoRecibido').value='';
}

function holdVenta(){
  if(carrito.length===0){ mostrarAlerta('No hay items para poner en espera', 'warning');; return; }
  const snapshot = {
    fecha: $('fechaVenta').value,
    tipoCliente: $('tipoCliente').value,
    docCliente: $('docCliente').value,
    descGlobal: parseFloat($('descGlobal').value||'0'),
    observaciones: $('observaciones').value,
    carrito
  };
  localStorage.setItem('venta_hold', JSON.stringify(snapshot));
  mostrarAlerta('Venta guardada en espera.', 'info');;
}

function finalizarVenta(){
  if(carrito.length===0){ mostrarAlerta('Agregá al menos un ítem.', 'warning');; return; }

  const payload = buildPayload();
  fetch('/ventas/guardar', {
    method:'POST',
    headers:{'Content-Type':'application/json'},
    body: JSON.stringify(payload)
  })
  .then(r => {
    if(!r.ok) throw new Error('Error al guardar la venta');
    return r.json();
  })
  .then(data=>{
    // alert(`Venta registrada. N°: ${data.numero} | ID: ${data.id}`);
    mostrarAlerta('Venta registrada. N°: ' + data.numero + ' - ID: ' + data.id, 'success');
    cancelarVenta();
  })
  .catch(err=>{
    console.error(err);
    mostrarAlerta('No se pudo registrar la venta.', 'danger');
  });
}

function buildPayload(){
  const total = parseFloat(($('total').textContent||'0').replace(',','.'));
  const recibido = parseFloat($('montoRecibido').value||'0');
  const descGlobalPorc = parseFloat($('descGlobal').value||'0');

  // Descomponer totales
  let subSinIVA = 0, ivaTotal = 0;
  carrito.forEach(it=>{
    const base = it.cantidad*it.precio;
    const desc = base*(it.descPorc/100);
    const neto = base - desc;
    const ivaMonto = neto*(it.iva/100);
    subSinIVA += neto;
    ivaTotal += ivaMonto;
  });
  const descGlobalMonto = (subSinIVA+ivaTotal)*(descGlobalPorc/100);

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
    vuelto: Number(Math.max(0, recibido-total).toFixed(2)),
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
(function(){
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
      `<div class="sug${i===selectedIdx?' active':''}" data-i="${i}">${p.codigo || ''} - ${p.nombre} <span class="precio">$${fmt(p.precioMin)}</span></div>`
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
      `<div class="sug${i===selectedIdx?' active':''}" data-i="${i}">${p.codigo || ''} - ${p.nombre} <span class="precio">$${fmt(p.precioMin)}</span></div>`
    ).join('');
  }

  function seleccionarProducto(p) {
    $('codigo').value = p.codigo || '';
    $('descripcion').value = p.nombre;
    $('precio').value = p.precioMin;
    $('cantidad').value = 1; // Siempre 1 al escanear
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



