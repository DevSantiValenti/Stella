package com.analistas.stella.model.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "cajas")
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime apertura;

    private LocalDateTime cierre;

    private BigDecimal montoInicial;

    private BigDecimal montoFinal;

    // private BigDecimal totalVentas;
    // private BigDecimal totalVueltos;
    private BigDecimal montoDeclarado; // Lo que el cajero cuenta
    private BigDecimal diferencia; // montoDeclarado - (montoInicial + totalVentas - totalVueltos)
    private String comentarioCierre; //Comentario opcional al cerrar la caja

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // puedes vincularlo con Usuario si quieres

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja_fisica", nullable = false)
    private CajaFisica cajaFisica;

    private Boolean abierta = true;
}
