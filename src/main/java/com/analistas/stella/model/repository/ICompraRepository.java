package com.analistas.stella.model.repository;

import com.analistas.stella.model.domain.Compra;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompraRepository extends CrudRepository<Compra, Long> {

}