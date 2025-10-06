document.addEventListener("DOMContentLoaded", () => {
    // Obtenemos los datos embebidos en el HTML
    const div = document.getElementById("topProductosData");
    const topProductos = JSON.parse(div.dataset.top);

    const tbody = document.querySelector("#tblTop tbody");
    tbody.innerHTML = "";

    topProductos.forEach((p, i) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
    <td>${i + 1}</td>
    <td>${p.nombre}</td>
    <td>${p.totalUnidades}</td>
    <td>$${p.totalImporte.toFixed(2)}</td>
    `;
        tbody.appendChild(tr);
    });
});


// ======== Helpers ========
const $ = s => document.querySelector(s);
const fmt = (n) => new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', maximumFractionDigits: 0 }).format(n);
const downloadPNG = (chart, filename) => {
    const link = document.createElement('a');
    link.href = chart.toBase64Image();
    link.download = filename;
    link.click();
};
const csvFromRows = (rows) => rows.map(r => r.map(v => `"${String(v).replace(/"/g, '""')}"`).join(',')).join('\n');
const exportTableCSV = (table, filename) => {
    const rows = [...table.querySelectorAll('tr')].map(tr => [...tr.children].map(td => td.innerText));
    const blob = new Blob([csvFromRows(rows)], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    link.click();
};

let chartVentas, chartPago;

function renderCharts() {
    const ventasDiv = document.getElementById("ventasDiariasData");
    const labels = JSON.parse(ventasDiv.dataset.labels);
    const data = JSON.parse(ventasDiv.dataset.data);

    const chartVentasCanvas = document.getElementById('ventasDiarias');
    chartVentas =  new Chart(chartVentasCanvas, {
        type: 'line',
        data: {
            labels: labels.map(f => new Date(f).toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit' })),
            datasets: [{
                label: 'Total vendido',
                data: data,
                fill: true,
                borderWidth: 2,
                tension: .3,
                backgroundColor: 'rgba(54,162,235,.15)',
                borderColor: 'rgb(54,162,235)',
                pointRadius: 3
            }]
        },
        options: {
            responsive: true,
            plugins: {
                tooltip: { callbacks: { label: ctx => `$${ctx.parsed.y.toFixed(2)}` } },
                legend: { display: false },
                title: { display: true, text: 'Ventas diarias ($)' }
            },
            scales: {
                y: { ticks: { callback: v => `$${v}` } }
            }
        }
    });

    // Método de pago (doughnut)
    const metodoPagoDiv = document.getElementById("metodoPagoData");
    const metodoPago = JSON.parse(metodoPagoDiv.dataset.metodoPago);

    chartPago = new Chart(document.getElementById('metodoPago'), {
        type: 'doughnut',
        data: {
            labels: metodoPago.map(m => m.metodo), // 👈 SIN .metodoPago
            datasets: [{
                data: metodoPago.map(m => m.total)
            }]
        },
        options: {
            plugins: {
                tooltip: {
                    callbacks: {
                        label: ctx =>
                            `${ctx.label}: ${ctx.parsed.toLocaleString('es-ES', {
                                style: 'currency',
                                currency: 'ARS'
                            })}`
                    }
                },
                title: {
                    display: true,
                    text: 'Distribución por método de pago'
                }
            },
            cutout: '60%'
        }
    });
}

// Descargar PNG de los gráficos
$('#dlVentas').addEventListener('click', () => downloadPNG(chartVentas, 'ventas-diarias.png'));
$('#dlPago').addEventListener('click', () => downloadPNG(chartPago, 'metodo-pago.png'));

// Exportar CSV de tablas
$('#csvTop').addEventListener('click', () => exportTableCSV($('#tblTop'), 'top_productos.csv'));
$('#csvStock').addEventListener('click', () => exportTableCSV($('#tblStock'), 'stock_critico.csv'));
$('#csvCategorias').addEventListener('click', () => exportTableCSV($('#tblCategorias'), 'top_categorias.csv'));

// Init
(function init() {
    renderCharts();
})();