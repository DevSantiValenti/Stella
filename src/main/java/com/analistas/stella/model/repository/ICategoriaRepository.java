package com.analistas.stella.model.repository;


import org.springframework.data.repository.CrudRepository;

import com.analistas.stella.model.domain.Categoria;

public interface ICategoriaRepository extends CrudRepository<Categoria, Long> {

}
