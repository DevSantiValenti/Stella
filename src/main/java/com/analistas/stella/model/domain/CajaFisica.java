package com.analistas.stella.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "cajas_fisicas")
public class CajaFisica {
 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Ejemplo: 1, 2, 3

    @Column(nullable = false, unique = true)
    private String nombre; // "Caja Principal", "Caja 2", etc.

    // Constructor vacío
    public CajaFisica() {}

    public CajaFisica(String nombre) {
        this.nombre = nombre;
    }
}
