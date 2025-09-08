package com.analistas.stella.model.domain;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data


public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String nombre;
    private String email;
    private String telefono;

}
