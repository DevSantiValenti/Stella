package com.analistas.stella.model.domain;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private Double precioMin;
    private Double precioBulto;
}
