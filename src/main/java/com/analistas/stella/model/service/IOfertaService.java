package com.analistas.stella.model.service;

import java.util.List;

import com.analistas.stella.model.domain.Oferta;

public interface IOfertaService {

    public void guardar(Oferta oferta);

    public List<Oferta> buscarTodos();

    public Oferta buscarPorId(Long id);

    public void borrarPorId(Long id);
    

}
