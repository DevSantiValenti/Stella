package com.analistas.stella.model.domain;

import lombok.Data;

import java.util.List;

@Data
public class CompraDTO {
    private String fecha;
    private Double subtotalSinIVA;
    private Double ivaTotal;
    private Double total;
    private String observaciones;
    private List<DetalleCompraDTO> items;
}