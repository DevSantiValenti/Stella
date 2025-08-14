package com.analistas.stella.model.domain;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "oferta")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacio")
    @Size(min = 2, max = 100, message = "El nombre completo debe tener entre 2 y 100 caracteres")
    private String descripcion;

    @ManyToMany
    @JoinTable(
        name = "oferta_producto", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "oferta_id"), // FK de Oferta
        inverseJoinColumns = @JoinColumn(name = "producto_id") // FK de Producto
    )
    private List<Producto> productos;

    @NotNull(message = "Insertar un precio válido")
    private Double precioPromocional;

    private Integer descuentoPorcentaje;

    private LocalDate fechaInicio;
    
    private LocalDate fechaFin;

    private String imagenBanner;
    
    private String condiciones;

    private boolean activo;

    @PrePersist
    public void PrePersist() {
        activo = true;
    }
}
