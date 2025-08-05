package com.analistas.stella.model.domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")

public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacio")
    @Size(min = 2, max = 100, message = "El nombre completo debe tener entre 2 y 100 caracteres")
    //@Column(name = "nombre_completo", length = 100)
    private String nombrecompleto;

    @NotEmpty(message = "El correo no puede estar vacio")
    @Email(message = "Debe ingresar un Correo valido")
    private String correo;

    @NotEmpty(message = "Ingrese una Contraseña")
    @Size(min = 6, message = "la contraseña debe tener al menos 6 caracteres")
    private String contraseña;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @NotEmpty(message = "Debe especificar un rol")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @PrePersist
    public void prePersist(){
        fechaAlta = LocalDate.now();
    }

}
