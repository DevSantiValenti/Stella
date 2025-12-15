package com.analistas.stella.model.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "detalles_compra")
public class DetalleCompra implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "iva_porcentaje", nullable = false)
    private Double ivaPorc;

    @Column(name = "descuento_porcentaje")
    private Double descuentoPorc;

    @Column(nullable = false)
    private Double subtotal;
}