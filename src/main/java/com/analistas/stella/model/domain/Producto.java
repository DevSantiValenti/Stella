package com.analistas.stella.model.domain;

import java.io.Serializable;
import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El código de barras no puede estar vacío")
    @Column(name = "cod_barra", length = 13)
    // @Max(13)
    private String codigoBarras;

    @NotEmpty(message = "El nombre del producto no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre del producto debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotNull(message = "El precio del producto no puede ser nulo")
    private Integer precioMin;

    @NotNull(message = "El precio del producto no puede ser nulo")
    private Integer precioBulto;

    @NotNull(message = "El stock del producto no puede ser nulo")
    private Integer stock;

    @NotNull(message ="El stock minimo no puede ser nulo")
    private Integer stockMin;

    @Column(name = "activo", columnDefinition = "boolean default 1")
    private boolean activo;

    // @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaVen;


    @Column(length = 1000)
    private String linkImagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Otros campos y métodos según sea necesario

    @PrePersist
    public void PrePersist() {
        activo = true;
    }

}
