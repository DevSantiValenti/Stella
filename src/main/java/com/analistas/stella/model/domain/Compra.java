package com.analistas.stella.model.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "compras")
public class Compra implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    @Column(name = "subtotal_sin_iva")
    private Double subtotalSinIVA;

    @Column(name = "iva_total")
    private Double ivaTotal;

    @Column(nullable = false)
    private Double total;

    @Column(length = 500)
    private String observaciones;

    @PrePersist
    public void prePersist() {
        if (fechaCompra == null) fechaCompra = LocalDateTime.now();
    }
}