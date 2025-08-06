package com.analistas.stella.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.analistas.stella.model.domain.Rol;

public interface IRolRepository extends JpaRepository<Rol, Long>{
    
    Rol findByNombre(String nombre);
}
