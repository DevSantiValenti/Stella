package com.analistas.stella.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.analistas.stella.model.domain.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);

}
