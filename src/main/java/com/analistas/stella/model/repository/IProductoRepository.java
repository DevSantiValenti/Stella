package com.analistas.stella.model.repository;


import org.springframework.data.repository.CrudRepository;

import com.analistas.stella.model.domain.Producto;
import java.util.List;


public interface IProductoRepository extends CrudRepository<Producto, Long> {

    List<Producto> findByActivo(boolean activo);

}
