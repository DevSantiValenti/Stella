package com.analistas.stella.model.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.analistas.stella.model.domain.Caja;
import com.analistas.stella.model.domain.CajaFisica;

public interface ICajaRepository extends CrudRepository<Caja, Long> {
    // Con esto puedo saber si hay una caja abierta
    // Optional<Caja> findByAbiertaTrue();

    // Con esto puedo saber si hay tal caja fisica abierta
    Optional<Caja> findByCajaFisicaAndAbiertaTrue(CajaFisica cajaFisica);

    // Encontrar caja por id para cerrar
    Optional<Caja> findByIdAndAbiertaTrue(Long id);


}
