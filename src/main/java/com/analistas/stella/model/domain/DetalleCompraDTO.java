package com.analistas.stella.model.domain;

import lombok.Data;

@Data
public class DetalleCompraDTO {
    private String codigo;
    private String descripcion;
    private Double cantidad;
    private Double precioUnitario;
    private Double ivaPorc;
    private Double descuentoPorc;
}