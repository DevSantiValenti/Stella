package com.analistas.stella.model.domain;

import lombok.Data;

@Data
public class DetalleVentaDTO {
    private String codigo;
    private String descripcion;
    private Double cantidad;
    private Double precioUnitaFrio;
    private Double ivaPorc;
    private Double descuentoPorc;
}
