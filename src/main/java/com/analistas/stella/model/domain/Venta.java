package com.analistas.stella.model.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ventas")

public class Venta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que registró la venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_cliente", nullable = false)
    private String tipoCliente; // MINORISTA o MAYORISTA

    @Column(name = "doc_cliente")
    private String docCliente;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "descuento_global_porcentaje")
    private Double descuentoGlobalPorc;

    @Column(name = "descuento_global_monto")
    private Double descuentoGlobalMonto;

    @Column(name = "subtotal_sin_iva")
    private Double subtotalSinIVA;

    @Column(name = "iva_total")
    private Double ivaTotal;

    @Column(nullable = false)
    private Double total;

    @Column
    private Double recibido;

    @Column
    private Double vuelto;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja")
    private Caja caja;

    @PrePersist
    public void prePersist() {
        if (fechaVenta == null)
            fechaVenta = LocalDateTime.now();
    }
}
